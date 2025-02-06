# Spring에서 ElastiCache로 Redis 적용하기

![](https://velog.velcdn.com/images/van1164/post/31ee0c43-13ad-40da-95d8-d45446949d67/image.png)

AWS ElastiCache는 Redis를 클라우드 환경에서 쉽게 사용할 수 있도록 도와주는 서비스이다.\
하지만 로컬 개발 환경에서 Elasticache에 접근이 안된다.\
또한, EC2에서도 Elasticache에 접근이 안되는 문제가 있었는데, 이에 대해 작성할 예정이다.

### 1. 로컬 환경에서 Redis 사용하기 <a href="#id-1-redis" id="id-1-redis"></a>

ElastiCache의 특성상 로컬 환경에서는 직접 접근할 수 없다. 따라서 로컬 개발을 할 때는 로컬 Redis 서버를 설치하고 이를 사용해야 한다. 일반적으로 localhost:6379와 같은 주소로 로컬 Redis 서버에 접근할 수 있다.

### 2. EC2 인스턴스에서의 Redis 접근 문제 <a href="#id-2-ec2-redis" id="id-2-ec2-redis"></a>

하지만 EC2에 Spring 프로젝트를 배포하려는데 redis 연결에 실패하였다. 그래서 EC2에서 직접 연결을 시도해보았다.

```bash
redis-cli -h ElastiCache 주소
```

EC2 인스턴스에서 redis-cli를 통해 ElastiCache에 접근할 때도 문제가 발생하였다.

왜 접근이 안되는지 보안 그룹이랑 이것저것을 다 뒤져봤는데 원인을 못찾다가 스택오버플로우에서 원인을 찾아냈다.

```sh
redis-cli -h ElastiCache 주소 --tls
```

뒤에 --tls를 붙여주니까 신기하게도 연결이 되었다.\
이유는 ElastiCache Redis는 보안상의 이유로 기본적으로 TLS를 통해서만 접근할 수 있도록 설정되어 있기 때문이었다.

### 3. Spring 애플리케이션 설정 <a href="#id-3-spring" id="id-3-spring"></a>

따라서 Spring Boot를 사용하여 ElastiCache Redis에 연결하기 위해서는 tls를 포함한 내용을application.properties 파일에 추가해야 한다.

```properties
spring.redis.host=elasticache-endpoint
spring.redis.port=6379
spring.redis.ssl=true
spring.redis.password=your-redis-password
```

여기서 spring.redis.ssl=true 설정을 통해 SSL 연결을 활성화하고, your-elasticache-endpoint에는 ElastiCache의 엔드포인트를 입력합니다.

Redisson을 사용할 경우

```properties
# redis://elasticache-endpoint
rediss://elasticache-endpoint
```

s를 하나 더 붙인 rediss 접두사를 사용해야한다.
