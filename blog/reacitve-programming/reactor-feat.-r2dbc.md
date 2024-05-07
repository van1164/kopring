# Reactor (feat. R2DBC)

![](https://velog.velcdn.com/images/van1164/post/537fa879-831b-4d57-842b-1105a732677c/image.png)

## R2DBC란? <a href="#r2dbc" id="r2dbc"></a>

> Reactive Relational DataBase Connectivity (R2DBC) 는 JPA,JDBC등으로 사용하던 Blocking 기반 RDB를 Reactive 하게 사용할 수 있도록 API 를 제공한다. WebFlux 와 R2DBC 를 사용하는 경우 애플리케이션이 온전히 Reactive 하게 동작할 수 있도록 지원한다.

### JDBC, JPA <a href="#jdbc-jpa" id="jdbc-jpa"></a>

`JDBC`는 동기 **Blocking I/O** 기반으로 설계된 데이터베이스 연결 라이브러리이다.\
`JPA`는 `JDBC`를 사용해서 데이터베이스와 객체를 잘 매핑해주는 기술로 **Blocking I/O**기반임은 똑같다.

이러한 이유로 WebFLux와 같이 **Non-Blocking** Reactive 환경에서 사용하게 되면 Blocking되는 현상이 발생한다.

### R2DBC <a href="#r2dbc" id="r2dbc"></a>

#### R2dbc SPI(Service Provider Interface) <a href="#r2dbc-spiservice-provider-interface" id="r2dbc-spiservice-provider-interface"></a>

**다음과 같은 인터페이스들 제공**

1. 연결 (Connection Factory 등등)
2. 에러 핸들링
3. transaction 관련 기능

**구문**

**`bind`**: sql에 파라미터를 binding하는 구문\
**`add`** : 이전 까지의 binding을 저장 후 새로운 binding생성\
**`execute`** : 생성된 binding만큼 쿼리를 실행 후 결과를 Publisher 형태로 제공

### R2DBC MySql <a href="#r2dbc-mysql" id="r2dbc-mysql"></a>

#### Connection <a href="#connection" id="connection"></a>

**MySqlConnectionFactory**

`MySqlConnection` 객체를 `Mono`형태로 가지고 있음.\
싱글톤 패턴으로 구현되어 있음.

**MySqlConnectionConfiguration**

MySQL 연결 설정을 포함하는 객체\
host, port, database, username, password등의 기본 설정을 제공

```
var connectionFactory = MySqlConnectionFactory.from(config);
```

#### Client <a href="#client" id="client"></a>

**R2dbcEntityTemplete**

spring data r2dbc의 추상화 클래스\
결과를 entity객체로 받을 수 있음.

```
var template = new R2dbcEntityTemplate(connectionFactory)
```

**R2dbcEntityOperations**

R2dbcEntityTemplate가 상속하고 있는 기능들에 대한 클래스이다.\
여기에는\
Select : ReactiveSelectOperation\
Insert : ReactiveInsertOperation\
Update : ReactiveUpdateOperation\
Delete : ReactiveDeleteOperation

**기본적인 예제**

**Select 예제**

```
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

**Update 예제**

```
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

**더 쉽게 사용하도록 구현된 방식**

```
	r2dbcTemplate.select(query,User::class.java) //selcet
    r2dbcTemplate.insert(user) // insert
    r2dbcTemplate.update(user) //update
```

### R2DBC Repository <a href="#r2dbc-repository" id="r2dbc-repository"></a>

JPA의 JPA Repository 인터페이스 처럼 R2DBC에서 CRUD와 같은 기본적인 기능들이 작성되어있는 인터페이스\
기본적인 기능이나 함수명등등 JPA Repository와 거의 동일함.

#### SimpleR2dbcRepository <a href="#simpler2dbcrepository" id="simpler2dbcrepository"></a>

R2DBC Repository의 기능들이 구현되어있는 구현체이다.

하지만 n개의 결과만 가져오는 등의 기능은 제공되지 않기 때문에 쿼리 메소드를 사용해야한다

#### Query Method <a href="#query-method" id="query-method"></a>

복잡한 쿼리들을 미리 작성해둬서 실행하는 방법 JPA에서 @Query어노테이션과 동일

**업데이트 예제**

```
@Modifying
@Query("update user set name = :name where id = :id")
fun updateNameById(String name, Long id) : Mono<Integer>
```
