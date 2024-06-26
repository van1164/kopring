# Redis Lua Script를 활용해서 주문 기능 원자성 보장

![](https://velog.velcdn.com/images/van1164/post/6cea3ea2-34a1-49c4-9f93-8896b60d0047/image.jpg)

주문기능을 구현하면서 많은 동시 요청이 들어왔을 때 재고 정합성 문제가 발생하였다. 기존에 구현했던 방식은 다음과 같다.

### 기존의 주문 기능 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/dbeb5bb4-e8d6-4217-9548-00b5ba6c79cc/image.png)

기존의 주문기능은 여러 개의 물건을 한번에 주문하였을 때 redis를 통해 재고를 바로 확인하고 재고가 있으면 주문을 하고 재고를 감소시키도록 구현하였다.

### 문제가 발생하는 상황 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/f207f539-9b97-4016-a69a-087bb646d892/image.png)

주문이 동시에 들어왔을 때, 첫 번째 주문에 대해서 id가 1인 물건에 대해서 재고확인을 한 후 두 번째 주문에 대해서 id가 1인 물건에 대해 재고확인을 하면, 둘 다 재고 확인에 성공할 것이다.\
이렇게 되면 5개의 재고밖에 없는데 7개의 주문만이 성공할 것이다.

#### 그럼 재고를 확인할 때 바로 재고를 감소시키면 어떨까? <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/b23743b0-3b98-4f88-87da-5bd239db3648/image.png)

위 플로우와 같이 두번째 주문에 대해 취소가 되기 때문에 문제가 없어 보인다. 하지만 이렇게 구현을 해도 문제가 발생한다.

![](https://velog.velcdn.com/images/van1164/post/a39b6883-c8c2-421c-b8bb-6d4c04dc8699/image.png)

만약에 첫번째 주문이 id 2인 물건에 대해 재고 부족으로 실패하였다고 가정해보자. 그러면 2개의 주문이 모두 실패하게 된다. 여기서 발생하는 문제는 크게 2가지이다.

* **재고가 남았음에도 주문실패.**\
  첫번째 주문이 결론적으로 실패하였음에도, 두번째 주문에서 id 1인 물건을 구매하지 못하였다. 사용자에게는 재고 부족이라는 메시지가 왔겠지만, 사실 재고가 남아있는 것이다.
* **기존 재고에 대한 롤백 필요**\
  같은 주문내에서 다른 물건에 대한 재고확인시 재고가 없으면 기존의 감소시켰던 재고를 다시 원상복구 시켜야한다.

### Lua Script를 활용해서 주문 기능 원자성 보장 <a href="#lua-script" id="lua-script"></a>

### Lua(루아) Script란? <a href="#lua-script" id="lua-script"></a>

![](https://velog.velcdn.com/images/van1164/post/9e67bcfc-9a9b-4a45-b7a5-f73246f5fd53/image.png)

Lua Script는 굉장히 작고 가벼운 인터프리터형 언어이다. 제일 큰 인터프리터가 300KB정도로 매우 작고 빠르다. Python, Ruby와 비교해도 수십배 정도가 더 빠르다.

### Redis에서 Lua Script <a href="#redis-lua-script" id="redis-lua-script"></a>

지금까지 Redis는 CLI 명령어로만 동작하는 줄 알았다. Spring에서 사용할 때도 Template만 사용해서 문제를 해결하려고 하였다.

하지만 Redis에서는 Lua Script를 통해 복잡한 명령에 대해서 원자성을 보장하면서 연산을 할 수 있다.

### Lua Script로 주문 기능 수정 <a href="#lua-script" id="lua-script"></a>

```
    String luaScript = "for i = 1, #ARGV, 2 do\n" +
            "    local key = KEYS[(i+1)/2]\n" +
            "    local max_stock = tonumber(ARGV[i])\n"+
            "    local increment = tonumber(ARGV[i + 1])\n" +
            "    local current_stock = tonumber(redis.call('GET', key) or '0')\n" +
            "    local new_stock = current_stock + increment\n" +
            "    if new_stock > max_stock then\n" +
            "        return redis.error_reply('Stock limit exceeded for key: ' .. key)\n" +
            "    end\n" +
            "    redis.call('SET', key, new_stock)\n" +
            "end\n" +
            "return 'OK'";

    public void redisStockUpdate(List<OrderRequest> orderRequestList, HashMap<Long,Integer> maxStockMap) throws RedisSystemException{
        HashMap<String,OrderStockDTO> keyCountMap =orderRequestListToHashMap(orderRequestList,maxStockMap);
        List<String> keyList = new ArrayList<>(keyCountMap.keySet());
        List<OrderStockDTO> orderStockDTOList = new ArrayList<>(keyCountMap.values());
        List<String> args = new ArrayList<>();
        for(OrderStockDTO orderStock : orderStockDTOList){
            args.add(orderStock.getMaxStock().toString());
            args.add(orderStock.getOrderCount().toString());
        }
        RedisScript<String> redisScript = new DefaultRedisScript<>(luaScript, String.class);

        redisTemplate.execute(redisScript, keyList, args.toArray());
    }
```

Lua Script를 활용해 재고를 확인하고 주문해서 재고를 감소시키는 동작을 주문별로 한번에 실행하도록 구현하였다. 이를 통해서 2가지 문제를 해결할 수 있었다.

* **주문별 원자성 보장을 통해 재고에 대한 정확도 보장**\
  Lua Script를 통해 주문별로 원자성을 보장하여 재고를 소진시키기 때문에 다른 주문과 동시에 요청이 들어와도 정합성의 문제를 일으키지 않는다.
* **주문별 원자성으로 인해 재고 부족시 주문에 대해 롤백**\
  같은 주문내에서 특정 물건의 재고 부족으로 소진되면 같은 주문이 모두 롤백되기 때문에 다시 되돌리는 작업을 수행하지 않아도 된다.

### 동작 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/f97a1abd-07ed-4443-954f-01696ae61e03/image.png)

***

### 결론 <a href="#undefined" id="undefined"></a>

Redis에서 Lua Script를 실행해서 주문별로 원자성을 보장하는 연산을 구현해 동시성문제를 해결할 수 있습니다. Redis가 싱글스레드로 동작하기 때문에 사용만으로 동시성 문제가 발생하지 않는다고 생각했지만 특정 기능에서는 원자성을 보장하지 않으면 동시성 문제가 발생할 수 있다는 것을 알게 되었다.

### 생각해보아야 할 문제 <a href="#undefined" id="undefined"></a>

주문이 동시에 정말 많이 들어오면 어떨까? Lua Script를 처음 사용해보는 거라 속도가 어떻게 될지 모르겠다. 레디스는 싱글 스레드로 동작하기 때문에 동시에 많은 요청이 왔을 때 각각을 빠르게 처리하지 못하면 DeadLock에 빠질 수 있을 것이다. 이는 다음에 부하테스트를 통해 알아보도록 하겠다.
