# JPA에서 R2DBC로 마이그레이션

![](https://velog.velcdn.com/images/van1164/post/286efaec-d284-45fe-8a36-7b98e14adce4/image.png)

> 리액티브한 프로그래밍을 위해 기존에 JPA로 작성되어있던 도메인들을 R2DBC로 변경하고자 한다.

### 기존 JPA 코드 <a href="#jpa" id="jpa"></a>

#### 도메인 <a href="#undefined" id="undefined"></a>

```kotlin
@Entity
@Table(name = "video")
data class Video(

    @Column(name = "title")
    var title : String,


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date,

    @Id
    @Column(name = "video_id")
    var id : String,

    @Column(name = "user_id")
    val userName : String,

    @Column(name = "thumbnail_url")
    var thumbNailUrl : String?,



    @Column(name = "comments")
    @ToString.Exclude
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    val commentList : MutableList<Comment> = mutableListOf(),


    @Column
    var good : Int = 0,

    @Column
    var bad : Int = 0,
)
```

#### Repository <a href="#repository" id="repository"></a>

```kotlin
@Repository
interface VideoRepository : JpaRepository<Video,String> {
}
```

### R2DBC로 변경 <a href="#r2dbc" id="r2dbc"></a>

JPA에서 사용하는 어노테이션을 모두 제거해주었다.

> 여기서 기존에 Id를 String으로 했었는데 Long으로 바꾸어주었다.\
> 이유는 R2DBC는 Id가 Long이 아닐경우 존재 유무로만 새 객체를 생성하고 아니면 Update 구문을 실행하기 때문이다.\
> [https://hiphopddori.tistory.com/127](https://hiphopddori.tistory.com/127)\
> 이 블로그 처럼 Persistable을 상속받아서 만들 수도 있지만, Video 도메인에 URL과 id가 따로 존재하는게 좋을 것같아서 분리하였다.

#### 도메인 <a href="#id-1" id="id-1"></a>

```kotlin
data class VideoR2dbc(
    var title : String,
    var url : String,
    val userName : String,
    var thumbNailUrl : String?,
    var good : Int = 0,
    var bad : Int = 0,
    @CreatedDate
    val createDate : LocalDateTime = LocalDateTime.now(),
    @Id
    val id : Long? = null
)
```

#### 테이블 생성 <a href="#undefined" id="undefined"></a>

R2DBC는 JPA처럼 테이블을 자동으로 생성해주지않기 때문에 직접 만들어줘야 한다ㅠㅠㅠ

```kotlin
create table video_r2dbc
(
    title          char(255) not null,
    user_name      char(255) not null,
    thumb_nail_url char(255) null,
    good           int       null,
    bad            int       null,
    create_date    datetime  null,
    id             bigint auto_increment primary key,
    url            char(255) not null
);
```

#### Repository <a href="#repository-1" id="repository-1"></a>

```kotlin
@Repository
interface VideoR2DBCRepository : R2dbcRepository<VideoR2dbc,String> {
    fun findFirstByUrl(url: String):Mono<VideoR2dbc>

}
```

### Service 동작 변경 <a href="#service" id="service"></a>

#### JPA 사용 서비스 <a href="#jpa" id="jpa"></a>

```kotlin
    @Transactional
    fun saveThumbnailData(fileUUID : String,thumbNailUrl : String) {
        val video = videoRepository.findById(fileUUID).getOrNull()
        if(vidoe!=null){
			video.thumbNailUrl = thumbNailUrl
		}
    }
```

#### R2DBC 사용 서비스 <a href="#r2dbc" id="r2dbc"></a>

```kotlin
    @Transactional("connectionFactoryTransactionManager")
    fun saveThumbnailData(fileUUID : String,thumbNailUrl : String): Mono<VideoR2dbc> {
        return videoRepository.findFirstByUrl(fileUUID)
            .doOnNext {
                it.thumbNailUrl =s3URL+"thumb/"+ thumbNailUrl
            }
            .flatMap{
                videoRepository.save(it)
            }
    }
```

### 발생한 문제 <a href="#undefined" id="undefined"></a>

```
No qualifying bean of type 'org.springframework.transaction.TransactionManager' available: expected single matching bean but found 2: transactionManager,r2dbcTransactionManager
org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.transaction.TransactionManager' available: expected single matching bean but found 2: transactionManager,r2dbcTransactionManager
```

#### 💔JPA와 R2DBC의 Transaction 매니저가 각각 있어서 발생 <a href="#jpa-r2dbc-transaction" id="jpa-r2dbc-transaction"></a>

위 R2DBC의 코드처럼 각각에 맞는 Transaction Manager를 작성해주면 된다.

**JPA**

```kotlin
@Transactional("transactionManager")
```

**R2DBC**

```kotlin
@Transactional("connectionFactoryTransactionManager")
```

### 확인 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/0ba2049b-e3d7-4066-ae99-fd4b29b2819f/image.png)\
저장이 잘된 것을 확인할 수 있었다.

> 기존에 작성했던 블로그 내용이 다음날 보니 다 날라가서..... 내용이 부실해졌네요.. 했던 내용중 더 기억나면 추가로 올리겠습니다 😅
