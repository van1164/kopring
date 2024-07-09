# Kopring
![image](https://github.com/van1164/kopring/assets/52437971/8403ddda-5f72-4df8-9f5d-69cc6aa02b70)


## 목차

### 1. [WebFlux](https://github.com/van1164/kopring/blob/main/webflux/1.%20%EB%8F%99%EA%B8%B0%EC%99%80%20%EB%B9%84%EB%8F%99%EA%B8%B0%20%26%20Blocking%EA%B3%BC%20Non-Blocking.md)
### 2. [R2DBC](#r2dbc)
### 3. [JPA](#jpa)
### 4. [Redis](#redis)
### 5. [Spring Security](#spring-security)
### 6. [Spring Batch](#spring-batch)
### 7. [Kafka](#kafka)
### 8. [AWS](#aws)
### 9. [Elastic Search](#elastic-search)
### 10. [JUNIT](#junit)
### 11. [ERROR](#error)

---

# 기타 프로젝트 및 학습
## 🍀 spring-webflux

* [Webflux 환경에서 MultiPart로 파일 업로드 (feat. S3 업로드하는 법까지)](spring-webflux/webflux-multipart-feat.-s3.md)
* [R2DBC 정리](spring-webflux/reactor-feat.-r2dbc.md)
* [Webflux에서 @Transactional이 Rollback을 안해요...](spring-webflux/webflux-transactional-rollback-....md)

## ♻️ 리액티브 프로그래밍 <a href="#reacitve-programming" id="reacitve-programming"></a>

* [동기 vs 비동기, Blocking vs Non-Blocking](<README (1).md>)
* [Executor Service와 Future, 그리고 CompletableFuture](reacitve-programming/executor-service-future-completablefuture.md)
* [Cold Sequence와 Hot Sequence](reacitve-programming/cold-sequence-hot-sequence.md)
* [Reactive Manifesto](reacitve-programming/reactive-manifesto.md)
* [Publisher와 Subscriber](reacitve-programming/publisher-subscriber.md)
* [Scheduler와 publishOn& subscribeOn](reacitve-programming/scheduler-publishon-and-subscribeon.md)
* [switchIfEmpty 와 defaultIfEmpty 차이](reacitve-programming/switchifempty-defaultifempty.md)

## 동영상 스트리밍 프로젝트 <a href="#project-video-streaming" id="project-video-streaming"></a>

* [FFmpeg 학습](project-video-streaming/ffmpeg.md)
* [Chunk로 나눠서 업로드](project-video-streaming/chunk.md)
* [HLS(Http Live Streaming) 적용기](project-video-streaming/hls-http-live-streaming.md)
* [썸네일 만들기](project-video-streaming/undefined.md)
* [업로드 시간 이게 최선일까? (feat.비동기 Non-Blocking)](project-video-streaming/feat.-non-blocking.md)
* [부하 테스트를 해보자 (feat. nGrinder)](project-video-streaming/feat.-ngrinder.md)
* [WebFlux적용기 (feat. Server Sent Event)](project-video-streaming/webflux-feat.-server-sent-event.md)
* [실시간 스트리밍 구현(feat. Nginx Rtmp)](project-video-streaming/feat.-nginx-rtmp.md)
* [JPA에서 R2DBC로 마이그레이션](project-video-streaming/jpa-r2dbc.md)
* [멀티모듈 적용기](project-video-streaming/undefined-1.md)
* [배포하면서 발생한 문제들](project-video-streaming/undefined-2.md)
* [댓글기능 구현](project-video-streaming/undefined-3.md)
* [댓글 좋아요 구현 (feat. 동시성 문제)](project-video-streaming/feat..md)
* [좋아요, 조회수 기능 최적화 (feat. Redis + AWS Lambda)](project-video-streaming/feat.-redis-+-aws-lambda.md)

## Spring

* [빈 스코프에 대해서](spring/undefined.md)
* [@Validated와 @Valid를 통한 유효성검사](spring/validated-valid.md)
* [bindingResult로 예외 확인](spring/bindingresult.md)
* [@NotNull, @NotEmpty, @NotBlank 의 차이점](spring/notnull-notempty-notblank.md)

## Spring-Jpa

* [N+1문제에 대한 생각](spring-jpa/n+1.md)
* [@Transactional의 내부 동작 과정](spring-jpa/transactional.md)
* [@Transactional에서 readOnly = true를 하면 무슨 일이 벌어질까](spring-jpa/transactional-readonly-true.md)
* [데이터베이스 격리수준과 동시성(feat. Locking과 MVCC 각각 관점에서)](spring-jpa/feat.-locking-mvcc.md)
* [Spring 코드에서의 트랜잭션과 DB에서의 트랜잭션](spring-jpa/spring-db.md)
* [JPA 페이지네이션 최적화 (Offset, Cusor, 커버링 인덱스)](spring-jpa/jpa-offset-cusor.md)

## SPRING-SECURITY-REACTIVE

* [Webflux환경에서 JWT 사용하기](spring-security-reactive/webflux-jwt.md)
* [@AuthenticationPrincipal에 null값 들어오는 문제](spring-security-reactive/authenticationprincipal-null.md)

## redis

* [좋아요 기능 최적화 (feat. redis)](redis/feat.-redis.md)
* [Redis Lua Script를 활용해서 주문 기능 원자성 보장](redis/redis-lua-script.md)

## 📗 토비의 스프링 <a href="#tobi-spring" id="tobi-spring"></a>

* [토비의 스프링 1장](tobi-spring/1.md)
* [토비의 스프링 2장](tobi-spring/2.md)
* [토비의 스프링 3장](tobi-spring/3.md)
* [토비의 스프링 4장](tobi-spring/4.md)
* [토비의 스프링 5장](tobi-spring/5.md)

## TDD

* [Fixture Monkey 적용기](tdd/fixture-monkey.md)
* [JUnit으로 동시성문제를 테스트할 수 있게 해보자! (라이브러리 개발기)](tdd/junit.md)

## 🎨 Design-Pattern

* [싱글톤 패턴](design-pattern/undefined.md)
* [팩토리 패턴](design-pattern/undefined-1.md)
* [전략 패턴](design-pattern/undefined-2.md)
* [옵저버 패턴](design-pattern/undefined-3.md)



# R2DBC

## R2DBC란?
> Reactive Relational DataBase Connectivity (R2DBC) 는 JPA,JDBC등으로 사용하던 Blocking 기반 RDB를 Reactive 하게 사용할 수 있도록 API 를 제공한다. WebFlux 와 R2DBC 를 사용하는 경우 애플리케이션이 온전히 Reactive 하게 동작할 수 있도록 지원한다.

### JDBC, JPA
`JDBC`는 동기 **Blocking I/O** 기반으로 설계된 데이터베이스 연결 라이브러리이다.
`JPA`는 `JDBC`를 사용해서 데이터베이스와 객체를 잘 매핑해주는 기술로 **Blocking I/O**기반임은 똑같다.

이러한 이유로 WebFLux와 같이** Non-Blocking** Reactive 환경에서 사용하게 되면 Blocking되는 현상이 발생한다.

## R2DBC
### R2dbc SPI(Service Provider Interface)
#### 다음과 같은 인터페이스들 제공
1. 연결 (Connection Factory 등등)
2. 에러 핸들링
3. transaction 관련 기능

#### 구문
**`bind`**: sql에 파라미터를 binding하는 구문
**`add`** : 이전 까지의 binding을 저장 후 새로운 binding생성
**`execute`** : 생성된 binding만큼 쿼리를 실행 후 결과를 Publisher 형태로 제공

## R2DBC MySql
### Connection
#### MySqlConnectionFactory
`MySqlConnection` 객체를 `Mono`형태로 가지고 있음. 
싱글톤 패턴으로 구현되어 있음.
#### MySqlConnectionConfiguration
MySQL 연결 설정을 포함하는 객체 
host, port, database, username, password등의 기본 설정을 제공

``` kotlin
var connectionFactory = MySqlConnectionFactory.from(config);
```

### Client

#### R2dbcEntityTemplete
spring data r2dbc의 추상화 클래스 
결과를 entity객체로 받을 수 있음.
```kotlin
var template = new R2dbcEntityTemplate(connectionFactory)
```

#### R2dbcEntityOperations
R2dbcEntityTemplate가 상속하고 있는 기능들에 대한 클래스이다.
여기에는 
Select : ReactiveSelectOperation
Insert : ReactiveInsertOperation
Update : ReactiveUpdateOperation
Delete : ReactiveDeleteOperation

#### 기본적인 예제
#### Select 예제
```kotlin
val r2dbcTemplate = R2dbcEntityTemplate(connectionFactory)

var query = Query.query(
	Criteria.where("name").is("ABC")
)

r2dbcTemplate.select(User::class.java)
             .from("user")
            .matching(query)
            .first()
            .doOnNext{
               it->log.info{it}
            }
            .subscribe()
```

#### Update 예제
```kotlin
val r2dbcTemplate = R2dbcEntityTemplate(connectionFactory)

var query = Query.query(
	Criteria.where("name").is("ABC")
)

var update = Update.update("name","XYZ")

r2dbcTemplate.update(User::class.java)
			.inTable("user")
            .matching(query)
            .apply(update)
            .doOnNext{
				it->log.info{it}
            }
            .subscribe()
```

#### 더 쉽게 사용하도록 구현된 방식
```kotlin
	r2dbcTemplate.select(query,User::class.java) //selcet
    r2dbcTemplate.insert(user) // insert
    r2dbcTemplate.update(user) //update
```

## R2DBC Repository
JPA의 JPA Repository 인터페이스 처럼 R2DBC에서 CRUD와 같은 기본적인 기능들이 작성되어있는 인터페이스
기본적인 기능이나 함수명등등 JPA Repository와 거의 동일함.

### SimpleR2dbcRepository
R2DBC Repository의 기능들이 구현되어있는 구현체이다.

하지만 n개의 결과만 가져오는 등의 기능은 제공되지 않기 때문에 쿼리 메소드를 사용해야한다

### Query Method
복잡한 쿼리들을 미리 작성해둬서 실행하는 방법 JPA에서 @Query어노테이션과 동일

#### 업데이트 예제
```kotlin
@Modifying
@Query("update user set name = :name where id = :id")
fun updateNameById(String name, Long id) : Mono<Integer>
```



# JPA 
## JPA 사용하는 이유
1. 생산성 => JDBC API와 SQL 문을 모두 작성해야하는 문제 해결
2. 유지 보수 => SQL에 의존하지 않기 때문에 수정할 코드가 줄어듦
3. 성능  => JPA라이브러리는 수많은 최적화로 내가 짜는거보다 성능 좋음!

## Entity 생성 기초

```kotlin
@Entity
@Table (name = "MEMBER")
data class Member (
    @Id
    @Column(name = "ID")
    private val id : String?,

)
```

### Column 어노테이션에 DDL 조건 추가

```kotlin
    @Id
    @Column(name = "ID", nullable = false, length = 16)
    val id : String,
```

### ENTITY에 JSON객체 사용하기

```kotlin
@Entity
@Table(name = "USER")
data class User(
    @Type(value = JsonType::class)
    @Column(name = "vote_list", columnDefinition = "longtext")
    val voteList: HashMap<String,Any>,
```

이렇게 Type을 JsonType으로 columnDefinition을 longtext로 설정하고 변수 타입을 HashMap으로 지정해주면 된다.


## 기본키 생성전략

### 기본키 직접할당 전략

```kotlin
val member = Member(  )
member.setId("id")  // id를 직접 넣어주는 방식
em.persist(member)
```
### IDENTITY 전략

```kotlin
data class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : String,
```

이 전략을 사용하면 데이터베이스가 자동으로 기본키를 생성하게 하는 전략으로 id를 쿼리를 데이터베이스에 전송한 후에 알 수있다.

영속 상태가 되기위해서는 id가 필요하기 때문에 em.persist()를 호출하는 즉시 데이터베이스에 전송된다.

### Sequence 전략

```kotlin
data class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GENERATOR")
    val id : String,
```

유일한 값을 순서대로 생성하는 시퀀스를 사용한 방식으로 오라클, H2등 시퀀스를 제공하는 DB에서만 사용가능.

IDENTITY와 다르게 em.persist()를 호출할 때 시퀀스를  사용해서 id를 조회해서 엔티티에 넣는다. 그후 commit을 하면 그때 디비에 저장된다.

### 테이블 전략

```kotlin
data class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.Table, generator = "SEQ_GENERATOR")
    val id : String,
```

SEQ_GENERATOR라는 이름의 테이블에 다음 시퀀스 값을 가지도록 만들어 놓고 그 테이블을 generator로 매핑한다.

그럼 그 테이블에서 자동적으로 원하는 엔티티에 id를 다음 시퀀스로 연결한다.

### Auto 전략

```kotlin
data class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.Auto)
    val id : String,
```
JPA가 데이터베이스에 따라 위의 전략들중 하나를 자동으로 선택한다.

## 연관관계 매핑 기초

### @ManyToOne
```kotlin
@Entity
@Table (name = "MEMBER")
data class Member (
    @ManyToOne
    @JoinColumn(name = "TEAM_ID") // 매핑할 컬럼명
    var team : Team? = null  // 매핑할 객체 선언
```

```kotlin
@Entity
@Table(name = "TEAM")
data class Team(
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID") // 매핑되는 컬럼명
    val id :Long? =null,
)
```

#### 테스트코드

```kotlin
@Test
fun createTeamAndMemberIntoTeam(){
	val team = service.createNewTeam("team1")  // Team객체 생성후 영속하는 함수
	val member = Member(name = "sihwan", passWord = "testPW")
	service.registerMember(member,team)
}
```

여기서 중요한 점은 팀을 member에 넣고 영속시키기 전에 팀을 먼저 영속시켜야 한다.

### @OneToMany + 양방향 매핑

```kotlin
@OneToMany(mappedBy = "team")
val members : MutableList<Member> = mutableListOf<Member>()
}
```
mappedBy는 연관관계를 갖는 다른 테이블에 필드를 쓴다.

mappedBy를 넣은 쪽은 연관관계의 주인이 아니기 때문에 수정을 할 수 없다.

```kotlin
@Entity
@Table (name = "MEMBER")
class Member (
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    var team : Team? = null
) {
    fun teamSet(team: Team) {
        if (this.team != null){
            this.team!!.members.remove(this)
        }
        this.team = team
        team.members.add(this)
    }
}
```

team을 넣는다고 해서 연관 테이블에 리스트에 추가되지 않기 때문에 직접 넣어주어야 한다.

### 연관관계에 있는 데이터 삭제
데이터를 삭제하고 싶을데 관계를 가지고 있는 테이블이 있으면 그 데이터와 연관된 곳에서 모두 영속을 해지해야 한다.

```kotlin
fun deleteTeam(teamName : String){
	val members = jpqlQuery.findMembersByTeamName(teamName)
	members?.forEach {
	    it.team = null
	}
	val team =jpqlQuery.findTeamByTeamName(teamName)
	em.remove(team)
}
```
이렇게 teamName을 가진 team을 삭제하고 싶을 때는  teamName을 가진 member들을 찾아서 member.team을 null로 바꿔주고 remove 해야한다.

## JPQL
JPQL은 엔티티 객체를 조회하는 객체지향 쿼리다.

### where절로 값찾기
```kotlin
fun findTeamByTeamName(teamName : String): Team? {
	val jpql = "select t from Team t where t.name =: name"
	return em.createQuery(jpql, Team::class.java)
	    .setParameter("name", teamName)
	    .singleResult  // 값이 한개일 경우
	// .resultList  // 값이 여러개일 경우
}
```
팀이름으로 팀 검색하는 쿼리

### 연관된 테이블 JOIN후 where절로 조건에 맞는 값 찾기
```kotlin
fun findMembersByTeamName(teamName: String): MutableList<Member>? {
	val jpql = "select m from Member m join m.team t where t.name =: teamName"
	return em.createQuery(jpql, Member::class.java)
	    .setParameter("teamName", teamName)
	    .resultList
}
```
특이하게 select *로 작성하면 안된다. Member타입의 m과 m.team타입의 t를 조인하고 where절로 조건을 추가하는 코드이다.

### jpql로 조회한 값을 DTO와 연결하기
```kotlin
val jpql = "select new 패키지명.DTO명(i.id,i.name) from Item i "
val voteList = em.createQuery(jpql,DTO명::class.java).resultList
}
```
여기서 특이한점은 JAVA와 같이 new를 사용하여야하고 DTO만 쓰면 안되며 패키지까지 써주어야한다.

### NamedQuery로 정적쿼리 사용하기

Entity에 NamedQuery를 작성하고

```kotlin
@Entity
@NoArgsConstructor
@NamedQuery(
    name = "User.findByEmail",
    query = "select u from User u where u.email =: email"
)
@Table(name = "USER")
data class User(
'''
)
```

다음과 같이 사용하면 된다.

```kotlin
val user = em.createNamedQuery("User.findByEmail",User::class.java)
		.setParameter("email,email).getSingleResult
```

### 서브쿼리

#### EXSITS

서브쿼리 결과가 존재하면 참.

```kotlin
val jqpl = "select m from Member m"
		+ "where exists(select t from m.team t where t.name = 'A')"
```

#### ALL, ANY

ALL은 서브쿼리 테이블 모든 값에 대해 조건이 만족해야 참.
ANY는 하나만 만족해도 참.

```kotlin
val jqpl = "select m from Member m"
		+ "where m.count > ALL (select n.count from NewMember n)" // m.count가 모든 n.count보다 커야지만 참.


val jqpl2 = "select m from Member m"
		+ "where m.count > ANY (select n.count from NewMember n)" // m.count가 n.count 하나보다만 크면 참.
```


## Criteria

JPQL보다 동적쿼리를 안전하게 생성하는 빌더 API
단, 가독성이 좀 떨어짐..

### 쿼리 생성

```kotlin
val cb = em.criteriaBuilder  //CriteriaBuilder
val cq = cb.createQuery(User::class.java) //CriteriaQuery
```

###= Select

##### jpql코드

```kotln
val userJpql = "select distinct u from User u where u.email =: email"
val user = em.createQuery(userJpql, User::class.java).setParameter("email", email).singleResult
```

##### Criteria 코드

```kotln
val cb = em.criteriaBuilder
val cq = cb.createQuery(User::class.java).apply {
    val u = from(User::class.java)
    select(u)
    where(cb.equal(u.get<String>("email"),email))
}
val user = em.createQuery(cq).singleResult

```

## QueryDSL

### 환경 설정
```kotlin
plugins {
	'''
	kotlin("kapt") version "1.9.22"
	idea
}


dependencies {
	//querydsl
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
}

idea {
	module {
		val kaptMain = file("${layout.buildDirectory}/generated/querydsl")
		sourceDirs.add(kaptMain)
		generatedSourceDirs.add(kaptMain)
	}
}

kapt {
	javacOptions {
		option("querydsl.entityAccessors", true)
	}
	arguments {
		arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor")
	}
}


```

### Projection을 활용한 DTO SELECT 예제

```kotlin
fun newLoadPopularVote(): MutableList<PopularVoteResponseDTO> {
	val voteList = queryFactory.select(
	    Projections.constructor(
		PopularVoteResponseDTO::class.java,
		vote.title,
		vote.voteUrl,
		vote.id,
		vote.mainImageUrl,
		vote.allVoteSum	
	    )			 	      //select
	).from(vote)
	    .where(vote.publicShare.isTrue)  //where
	    .orderBy(			    //order
		vote.allVoteSum.desc()
	    )
	    .limit(5)			   //limit
	    .fetch()
	return voteList
}
```



# Redis

## RedisConfig 작성
```kotlin
@Configuration(value = "redisConfig")
@EnableRedisRepositories
@RequiredArgsConstructor
class RedisConfig {

    @Value("\${spring.data.redis.host}")
    var host : String

    @Value("\${spring.data.redis.port}")
    var port : Int


    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory? {
        val lettuceConnectionFactory = LettuceConnectionFactory(host, port)
        lettuceConnectionFactory.start()
        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }
}
```
## RedisRepository 구현

```kotlin
@Repository
class RedisRepository {

    val redisTemplate by lazy { RedisConfig().redisTemplate() }

    fun save(jwt : String, email : String){
        redisTemplate.opsForValue().set(jwt,email)
    }

    fun loadByJwt(jwt : String): String? {
        return redisTemplate.opsForValue().get(jwt)
    }

}
```
# ReactiveRedis

## ReactiveRedisConfig
```kotlin
    @Bean
fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
	val lettuceConnectionFactory = LettuceConnectionFactory(host, port)
	lettuceConnectionFactory.start()
	return lettuceConnectionFactory
}
```

## ReactiveRedisRepository
```kotlin
@Repository
class RedisR2dbcRepository(
    private val redisTemplate : ReactiveRedisTemplate<String,String>,
    private val mapper: ObjectMapper
) {

    fun save(key : String, value : String, duration: Duration): Mono<Boolean> {
        return redisTemplate.opsForValue().set(key,value, duration)
    }

    fun load(key:String): Mono<String> {
        return redisTemplate.opsForValue().get(key)
    }

    fun <T> load(key : String, type : Class<T>): Mono<T> {
        return redisTemplate.opsForValue()
                            .get(key)
                            .map { value-> mapper.readValue(value,type) }
    }

    fun removeRtmp(streamKey: String): Mono<String> {
        return redisTemplate.opsForValue().getAndDelete(streamKey)
    }

}
```


---

# Spring Security

## OAuth 2.0

### Google

#### OAuth 유저 서비스 커스텀 구현
```kotlin
@Service
class OAuth2UserService : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
	// 동작
        return super.loadUser(userRequest)
    }
}
```
OAuth로 사용자 받아오는 서비스 구현

#### SecurityConfig 파일 구현

```kotlin
import org.springframework.security.config.annotation.web.invoke
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http { // kotlin DSL
            httpBasic { disable() }
            csrf { disable() }
            cors { }
            authorizeRequests {
                authorize("/user/**", hasAuthority("ROLE_USER"))
            }
            oauth2Login {
                loginPage = "/loginPage"
                defaultSuccessUrl("/",true)
                userInfoEndpoint {  }
            }
        }
        return http.build()
    }
```
websecurityconfigureradapter가 Deprecated되면서 Kotlin은 Kotlin DSL을 사용해야 하게 됨.

따라서

import org.springframework.security.config.annotation.web.invoke 를 꼭 넣어줘야함

## SuccessHandler 구현

```kotlin
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
		'''
            oauth2Login {
                '''
                authenticationSuccessHandler = OAuthSuccessHandler()
            }
```
filterChain에 http.oauth2Login 에 authenticationSuccessHandler를 추가하고 핸들러를 등록한다.

```kotlin
@Component(value = "authenticationSuccessHandler")
class OAuthSuccessHandler : AuthenticationSuccessHandler {
    // OAuth로그인후 불러와서 할 동작구현
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val oAuth2User = authentication.principal as OAuth2User
        val name = oAuth2User.attributes["name"] as String
        val email = oAuth2User.attributes["email"] as String
}
    }
}
```
## Filter추가로 JWT 토큰 검증하기

#### addFilterBefore로 추가한다

```kotlin
class SecurityConfig(val oAuthSuccessHandler: OAuthSuccessHandler, val oAuthFailureHandler: OAuthFailureHandler) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
	'''
            addFilterBefore<UsernamePasswordAuthenticationFilter> (JwtAuthenticationFilter(JwtTokenProvider()))
        }
        return http.build()
    }
}
```

#### JwtAuthenticationFilter 구현

```kotlin
class JwtAuthenticationFilter(
        private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = resolveToken(request as HttpServletRequest)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
            println("doFilterChain:$authentication")
        }
        chain?.doFilter(request, response)
    }

    private fun resolveToken(request : HttpServletRequest) : String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

}
```

# Spring Batch

## 의존성 추가

```kotlin
	//Spring Batch
	implementation("org.springframework.boot:spring-batch-test")
	implementation("org.springframework.boot:spring-boot-starter-batch")
```


## 기초 Configuration

```kotlin
@Configuration
class JobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val tasklet: VoteTasklet
) {
    @Bean
    fun job(): Job {
        return JobBuilder("job", jobRepository)
            .start(step())
            .build()
    }
    @Bean
    fun step(): Step {
        return StepBuilder("step", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build()
    }
}
```

## 기초 Tasklet 정의
```kotlin
@StepScope
@Component
class VoteTasklet(
    val userRepository: UserRepository,
    val userRankingRepository: UserRankingRepository
): Tasklet {
    val log = KotlinLogging.logger{}
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        log.info { "tasklet start" }

        //read
        val rankingList = userRepository.loadRanking()


        //process
        val userIdList = rankingList.map{it.id}


        //write
        userRankingRepository.resetAndSave(userIdList)

        return RepeatStatus.FINISHED
    }
}
```

## @Scheduled를 사용해 주기적으로 실행하기

```kotlin
@Component
class SchedulerConfig(
    val jobLauncher: JobLauncher,
    val jobConfig: JobConfig
) {
    val log = KotlinLogging.logger {  }
    @Scheduled(fixedRate  = 1000) //임시로 10초마다 생성
    fun popularVoteRenew(){
        log.info{"RankingRenew Start"}
        try {
            jobLauncher.run(jobConfig.job(),JobParameters())
        } catch (e: JobExecutionAlreadyRunningException) {
            log.error(e.message)
        } catch (e: JobInstanceAlreadyCompleteException) {
            log.error(e.message)
        } catch (e: JobParametersInvalidException) {
            log.error(e.message)
        } catch (e: JobRestartException) {
            log.error(e.message)
        }
        log.info{"RankingRenew End"}
    }
}
```

# Kafka

## Config파일 작성

### KafkaTemplate 빈 등록

```kotlin
@Configuration
@EnableKafka
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    var bootStrapServers : String
) {


    @Bean
    fun kafkaTemplate() : KafkaTemplate<String, Any> {
        return KafkaTemplate<String,Any>(producerFactory());
    }
```

### ProducerFactory 빈 등록

```kotlin
@Bean
fun producerFactory() : ProducerFactory<String,Any>{
	val producerConfig = HashMap<String,Any>()
	producerConfig[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootStrapServers
	producerConfig[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
	producerConfig[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
	return DefaultKafkaProducerFactory(producerConfig)
}
```

### ConsumerFactory 빈 등록

```kotlin
@Bean
fun consumerFactory() : ConsumerFactory<String,Any>{
	val consumerConfig = HashMap<String,Any>()
	consumerConfig[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootStrapServers
	consumerConfig[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
	consumerConfig[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
	return DefaultKafkaConsumerFactory(consumerConfig)
}
```

### ConcurrentKafkaListenerContainerFactory 빈 등록

#### Consumer가 Listner를 통해 메시지가 들어오는지 받아올 수 있도록 하는 객체

```kotlin
@Bean
fun kafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, Any>{
	val conCurrentListener = ConcurrentKafkaListenerContainerFactory<String,Any>()
	conCurrentListener.consumerFactory = consumerFactory()
	return conCurrentListener
}
```
## 프로듀서 서비스 구현

```kotlin
@Service
class KafkaProducerService(
    val kafkaTemplate : KafkaTemplate<String,Any>
) {

    val testTopic = "testTopic"

    fun pub(msg : String){
        kafkaTemplate.send(testTopic,msg)
    }

}
```

## 컨슈머 리스너 구현

```kotlin
@Service
class KafkaConsumerService {
    private val log = KotlinLogging.logger {  }
    @KafkaListener(topics= ["testTopic"], groupId = "kafkaTest")
    fun consumer(msg: String) {
        log.info { "KafkaConsumer: $msg" }
    }
}
```

# Kafka Streams

## 환경구성

```kotlin
@EnableKafkaStreams
@EnableKafka
@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    var bootStrapServers : String
) {
    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kafkaStreamConfig() : KafkaStreamsConfiguration{
        val kStreamConfig = hashMapOf<String,Any>()
        kStreamConfig[StreamsConfig.APPLICATION_ID_CONFIG] = "stream-test"
        kStreamConfig[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootStrapServers
        kStreamConfig[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        kStreamConfig[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        kStreamConfig[StreamsConfig.NUM_STREAM_THREADS_CONFIG] =1
        return KafkaStreamsConfiguration(kStreamConfig)
    }
}
```

## 서비스 기초구성

```kotlin
@Service
class KafkaStreamService {

    val stringSerde: Serde<String> = Serdes.String()

    @Autowired
    fun buildPipeline(sb : StreamsBuilder) {
        val kStream = sb.stream("testTopic", Consumed.with(stringSerde, stringSerde))
        kStream.filter { key, value ->
            value.contains("test")
        }.to("testStream")
    }
}
```

```text
testTopic으로 들어오는 메시지를 컨슘해서 값에 
test가 들어가는 값을 testStream Topic으로 메시지를 보낸다.
```

# 엔티티 메니저
## 엔티티 매니저 설정

```kotlin
	val emf = Persistence.createEntityManagerFactory("jpaTest")
	val em = emf.createEntityManager()
```

## 트랜잭션 관리

```kotlin
	val tx = em.transaction
	try {
		tx.begin()
		logic(em)
		tx.commit()
	} catch (e: Exception) {
		tx.rollback()
	} finally {
		em.close()
	}
```

## repository에서 em과 tx 활용

```kotlin
class MemoryMemberRepository : MemberRepository {

    override val em: EntityManager
        get() = EntityManagerObject.em
    override val tx: EntityTransaction
        get() = EntityManagerObject.tx

    override fun save(member: Member) {
        tx.begin()
        em.persist(member)
        tx.commit()
    }

    override fun findById(id: String): Member {
        return em.find(Member::class.java, id)
    }

}
```

## @Transactional과 @PersistenceContext를 활용한 중복코드 제거

#### 엔티티 매니저 의존성 주입 @PersistenceContext

``` kotlin
@Repository
class BaseRepository {
    @PersistenceContext
    lateinit var em : EntityManager
}
```

#### Transaction 반복코드 @Transactional로 대체

``` kotlin
/*
tx.begin()
---
tx.commit()
*/

위와 같은 역할을 @Transactional이 대신함.

@Transactional
class UserService(val userRepository: UserRepository):BaseService() {
```

## 코루틴을 사용하는 suspend function은 서비스 계층에서 한번에 @Transactional이 적용되지 않음.

### 적용방법 추가예정

#### 현재 방식

##### 각 Repository 함수마다 @Transactional을 추가해준다.

# Elastic Search

## Dependency
```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
```

## Config
```kotlin
@Configuration
@Import(DataSourceAutoConfiguration::class)
class ElasticConfig : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .build()
    }
}
```

## Repository
```kotlin
@Repository
interface TestElasticSearch : ElasticsearchRepository<TestUserElastic,Long> {
    //fun getTestUserElasticsByEmailContains(email: String) : List<TestUserElastic>
}
```

# AWS

## S3

### S3Config 작성
```kotlin
@Configuration
class S3Config(
        @Value("\${aws.s3.accessKey}")
        private val accessKey: String,
        @Value("\${aws.s3.secretKey}")
        private val secretKey: String,
) {
    @Bean
    fun amazonS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(
                        AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey))
                )
                .withRegion(Regions.AP_NORTHEAST_2)
                .build()
    }
}
```

### coroutine사용한 여러 이미지 업로드 컨트롤러
```kotlin
@RestController
@RequestMapping("/")
class S3TestController(val amazonS3Client : AmazonS3) {
    @PostMapping("/multipart-files")
    suspend fun uploadMultipleFilesWithCoroutine(
            @RequestPart("uploadFiles") multipartFiles: List<MultipartFile>,
            @RequestParam type: String,
    ) = withContext(Dispatchers.IO) {
        val uploadJobs = multipartFiles.map {
            val objectMetadata = ObjectMetadata().apply {
                this.contentType = it.contentType
                this.contentLength = it.size
            }
            async {
                val putObjectRequest = PutObjectRequest(
                        "vote-share",
                        UUID.randomUUID().toString() + type,
                        it.inputStream,
                        objectMetadata,
                )
                amazonS3Client.putObject(putObjectRequest)
            }
        }
        uploadJobs.awaitAll()
        return@withContext "test Complete"
    }
}
```

# JUNIT

## Controller테스트

mockMvc를 사용해서 컨트롤러 테스트를 할 수 있다.

``` kotlin

lateinit var mockMvc: MockMvc

@Test
@WithMockUser()
fun getMyPage() {
	mockMvc.perform (
	    get("URL")
		.contentType(MediaType.APPLICATION_JSON)
		.header("Authorization","TestJWT")
	).andExpect(status().isOk)
	    .andExpect(jsonPath("$.email").value(testEmail))
	    .andExpect(jsonPath("$.accessToken").value(testJwt))
    .andExpect(jsonPath("$.nickName").value(testName))

}
```

## 비동기 처리된 컨트롤러 테스트방법 (feat. test Response가 빈값일 경우 이에 해당함.)

#### 위와 다르게 perform을 먼저하고 asyncDispatch를 통해서 진행해야한다.

``` kotlin
val mvcResult = mockMvc.perform(
    multipart("/api/v1/vote/create_vote")
	.file(testImage)
	.file(testImages)
	.file(voteDTO)
	.contentType(MediaType.MULTIPART_FORM_DATA)
	.header("Authorization", testJwt.grantType + " " + testJwt.accessToken)
    ).andExpect(status().isOk)
    .andExpect(request().asyncStarted())
    .andExpect { request().asyncResult("body") }
    .andReturn()

mockMvc.perform(asyncDispatch(mvcResult))
    .andExpect(status().isOk)
    .andExpect(jsonPath("$.반환값").조건)


```

[참고] https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework/async-requests.html



# ERROR

## Unable to load class [org.h2.Driver] 
h2 사용시 생기는 오류로 build.gradle.kts에 의존성 추가로 해결
```kotlin
	runtimeOnly ("com.h2database:h2")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
```

## Unable to locate persister
JPA가 자동으로 Entity 클래스를 불러오지 못하는 상황이 생겼다.

여러가지 방법을 시도했지만 안됐고, 해결한 방법은 persistence.xml에 직접 class를 추가해준 것이다.

```xml
    <persistence-unit name="jpaTest">
        <class> com.shan.kopring.data.model.Member</class> //직접 추가한 부분
        <properties>
		'''

persistence.xml
```

## Could not find mysql:mysql-connector-java
mysql 연동하는 과정에서 생긴 오류이다. 이유는 MySQL 8.0.31부터 클래스가 변경되었다. 따라서

```kotlin
dependencies {
	//implementation ("mysql:mysql-connector-java") 변경전
	implementation ("com.mysql:mysql-connector-j")  // 변경후
```

## org.hibernate.PersistentObjectException: detached entity passed to persist
```kotlin
data class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long? = null,
```
이렇게 기본자생성 전략을 선택한 상태에서 직접 id를 넣어줄 경우 오류 발생함.

##  Type javax.servlet.http.HttpServletRequest not present
Spring Boot 3.XX 버전에서 Swagger를 적용시킬 때 생긴 오류

```kotlin
implementation("io.springfox:springfox-boot-starter:3.0.0")  // springfox 업데이트 안됨
```

springfox가 아닌 springdoc을 사용하면 오류 없이 사용가능하다.

```kotlin
//swagger
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
implementation("io.swagger.core.v3:swagger-annotations:2.2.16")
```

## com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure

docker에서 mysql을 연동할 때생긴 오류

application.properties에서 mysql주소를 localhost가 아닌 mysql 컨테이너 이름으로 설정시 DNS사용으로 해결

#### 이때 중요한건 application.properties와 persistence.xml에서도 디비를 변경해주어야한다.

```
spring.datasource.url=jdbc:mysql://my:3306/database-name
 <property name="javax.persistence.jdbc.url" value="jdbc:mysql://mysql:3306/database-name"/>
```


## 연관된 테이블 참조시 무한 루프와 StackOverFlow가 생기는 경우

Json으로 바꾸는 과정에서 서로 무한으로 불러오기 때문에 생기는 문제.
@JsonBackRefernece를 추가해주어서 그 컬럼을 json으로 바꾸지 않을 수있음.

``` kotlin
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonBackReference
    val teamList: MutableList<Team> = mutableListOf(),
```

## org.springframework.dao.CannotAcquireLockException: PreparedStatementCallback;

#### Spring Batch + @Scheduled 사용시 DeadLock과 함께 이러한 오류가 발생하였다.

```kotlin
@EnableBatchProcessing
class JobConfig(
```

#### @EnableBatchProcessing 이걸 Job 설정 클래스 맨위에 작성해주어야한다.

## Error : Timeout waiting for connection from pool

```text
amazonS3 *S3Object* 를 *close* 해주지 않았기 때문에 다음과 같은 오류가 발생하였다. 

S3Object는 Closeable을 implements하고 있기 때문에 *try-with-resources* 를 사용할 수있다.
```
*try-with-resources* 란 AutoCloseable 인터페이스를 구현하고 있는 자원에 대해 try안에 그 자원을 넣으면 작업이 끝나면 자동으로 close해주는 것을 말한다.



#### 코틀린에서 사용법!
코틀린에서는 use 고차함수를 통해 사용할 수 있다.
```kotlin
fun readFirstLine(path: String): String {
    BufferedReader(FileReader(path)).use { br ->
        return br.readLine()
    }
}
```
## com.amazonaws.ResetException: The request to the service failed with a retryable reason, but resetting the request input stream has failed. See exception.getExtraInfo or debug-level logging for the original failure that caused this retry.;  If the request involves an input stream, the maximum stream buffer size can be configured via request.getRequestClientOptions().setReadLimit(int)

### 이 오류는 AWSS3에 파일을 PUT할 때 inputStream으로 업로드할 때 생기는 버그라는 것을 알아냈다. 아래는 참고한 사이트이다.
https://stackoverflow.com/questions/30121218/aws-s3-uploading-large-file-fails-with-resetexception-failed-to-reset-the-requ

```kotlin
        val thumbNailFile = File(thumbNailPath)
        val request = PutObjectRequest(
            "video-stream-spring",
            thumbNailPath,
            thumbNailFile.inputStream(),
            ObjectMetadata()
        )
```
### 이렇게 작성했던 코드를

```kotlin
        val thumbNailFile = File(thumbNailPath)
        val request = PutObjectRequest(
            "video-stream-spring",
            thumbNailPath,
            thumbNailFile,
        )
```
### inputStream이 아닌 그냥 파일 자체로 업로드하면 오류가 해결되었다.
