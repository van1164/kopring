# 좋아요 기능 최적화 (feat. redis)

![](https://velog.velcdn.com/images/van1164/post/5fd1abf8-27dd-47df-9e27-b8dec0ad6ab2/image.png)

앞선 블로그에서 좋아요 기능을 좋아요 테이블과 Join과 Count 쿼리를 통해 좋아요 수를 조회하였다. 하지만 100만개 정도의 좋아요를 가진 테이블을 조회할 때 아무리 쿼리를 최적화 하여도 2초 이상의 시간이 걸렸다. 하나를 조회하는 것에서도 이정도이니 좋아요 수와 함께 리스트를 조회하려면 더 많은 시간을 소요해야한다. 따라서 이를 최적화하는 과정을 기록하고자 한다.

#### Service <a href="#service" id="service"></a>

redis의 increment를 사용해서 좋아요 수를 증가 시킨다.

```java
@Transactional
public void likeTrip(Long tripId, Long userId) throws InterruptedException {
    String key = LIKE_KEY_PREFIX + tripId;
    tripLikeRepository.save(TripLike.builder().tripId(tripId).registeredAt(LocalDateTime.now()).userId(userId).build());
    likeRedisTemplate.opsForValue().increment(key);
    Thread.sleep(10000L);
}
```

#### 스케쥴링 <a href="#undefined" id="undefined"></a>

1분마다 redis에 있는 좋아요수를 모두 불러와서 db에 batch update를 통해 한번에 업데이트 했다.

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class TripLikeBatchService {

    private final RedisTemplate<String, Object> likeRedisTemplate;
    private final JdbcTemplate jdbcTemplate;

    private final String sql = "UPDATE trip SET like_count = ? where id = ?";
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncLikes() {
        log.info("scheduled");
        try{
            long startTime = System.currentTimeMillis();
            Set<String> keys = likeRedisTemplate.keys(LIKE_KEY_PREFIX + "*");
            List<Object[]> batchArgs = new ArrayList<>();
            if (keys != null) {
                for (String key : keys) {
                    Long tripId = Long.valueOf(key.replace(LIKE_KEY_PREFIX, ""));
                    Integer likeCount = (Integer) likeRedisTemplate.opsForValue().get(key);
                    if (likeCount != null && likeCount > 0) {
                        batchArgs.add(new Object[] {likeCount,tripId});
                    }
                }
                jdbcTemplate.batchUpdate(sql,batchArgs);
                long endTime = System.currentTimeMillis();
                log.info("실행 시간 : " + (endTime - startTime));
            }
            else{
                log.info("keys 가 null");
            }
        }
        catch (EntityNotFoundException e){
            log.error(e.getMessage());
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

    }

}
```

#### K6를 활용한 테스트 <a href="#k6" id="k6"></a>

![](https://velog.velcdn.com/images/van1164/post/125f3883-473a-44a3-a416-273a95cdbf73/image.png)

30초간 100명의 사용자로 동시 요청 테스트를 진행한 결과 32658번의 요청에 대해서 모두 성공적이었다.

**데이터 베이스 확인**

![](https://velog.velcdn.com/images/van1164/post/e7e6db5c-b8c0-46c9-93bf-a18cb1636f8e/image.png)

스케쥴링한 서비스가 끝난 후에 조회해보니 오차없이 정확한 좋아요 수가 저장되어 있는걸 확인할 수 있었다.

**스케쥴링 속도**

![](https://velog.velcdn.com/images/van1164/post/fffb43c7-c078-44ee-b2b6-cda4900900c9/image.png)\
같은 포스트에 대해 좋아요를 동시 요청하였을 때 정합성문제가 없었고, 요청 응답속도도 좋았다. 다만 스케쥴링이 현재는 모든 포스트에 대해 처리를 하고 있어서 76초 정도 소요되는 것을 확인했다.

### 결론 <a href="#undefined" id="undefined"></a>

현재로써 redis에 좋아요들을 저장하고 주기적으로 좋아요를 업데이트해주는 과정을 거친다. 실제 서비스를 운영하려면 이 배치 시스템을 어플리케이션 서버와 분리 시켜서 독립적으로 동작하도록 구현해야할 것같다.

또한 좋아요 수를 업데이트하는 과정에서 계속 Lock이 걸릴텐데 좋아요수를 업데이트하는 일은 이 배치 동작밖에 없기 때문에 격리 수준을 최대한 낮춰서 쿼리를 수행하는 것을 고려해볼 예정이다.
