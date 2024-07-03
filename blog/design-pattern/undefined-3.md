# 옵저버 패턴

### ✍ 옵저버 패턴 <a href="#undefined" id="undefined"></a>

**옵저버 패턴**은 주체가 어떤 객체의 상태변화를 관찰하다가 상태 변화가 있을 때마다 변화를 알려주는 디자인 패턴이다.

![](https://velog.velcdn.com/images/van1164/post/ad209e46-e511-4513-a678-6551374ddf46/image.png)

유튜브와 같은 구독형 서비스가 있다고 하면, 사용자가 구독을 누르면 유튜버에게 알림을 보내는 기능이 필요하다.

#### 💻 코드 예시 <a href="#undefined" id="undefined"></a>

유튜버와 구독자는 양방향으로 알림이 가야하기 때문에 Publisher와 Observer를 모두 상속 받도록 구현해 보았습니다.

**인터페이스**

```java
public interface Observer {
    public void update(String fromName, String msg);
}

public interface Publisher {
    public <T extends Publisher & Observer> void add(T observer);
    public <T extends Publisher & Observer> void delete(T observer);
}
```

**유튜버**

```java
public class Youtuber implements Publisher, Observer{
    private ArrayList<Observer> subscribers;
    private String youtuberName;

    public Youtuber(String youtuberName){
        subscribers = new ArrayList<>();
        this.youtuberName = youtuberName;
    }

    @Override
    public <T extends Publisher & Observer> void add(T subscriber) {
        this.subscribers.add(subscriber);
        subscriber.add(this);
    }

    @Override
    public <T extends Publisher & Observer> void delete(T subscriber) {
        this.subscribers.remove(subscriber);
        subscriber.delete(this);
    }

    public void startLiveStreaming() {
        this.subscribers.forEach(subscriber -> {
            subscriber.update(youtuberName,"방송을 시작했습니다.");
        });
    }

    @Override
    public void update(String fromName, String msg) {
        System.out.println("=================유튜버 화면=================");
        System.out.println(fromName+"님이 " + msg);
        System.out.println("===========================================");
    }
}
```

**구독자**

```java
class Subscriber implements Observer, Publisher {
    private String name;
    private List<Observer> youtubers;

    public Subscriber(String name) {
        this.name = name;
        this.youtubers = new ArrayList<>();
    }

    @Override
    public void update(String fromName, String msg) {
        System.out.println("=================구독자 화면=================");
        System.out.println(fromName+"님의 알림!!!");
        System.out.println(msg);
        System.out.println("===========================================");
    }

    @Override
    public <T extends Publisher & Observer> void add(T youtuber) {
        youtubers.add(youtuber);
        youtuber.update(name,"구독하였습니다.");
    }

    @Override
    public <T extends Publisher & Observer> void delete(T youtuber) {
        youtubers.remove(youtuber);
    }
}
```

**실행**

```java
public class Main {
    public static void main(String[] args) {
        Youtuber youtuber = new Youtuber("재밌는 유튜버");

        Subscriber subscriber1 = new Subscriber("name A");
        Subscriber subscriber2 = new Subscriber("name B");

        youtuber.add(subscriber1);
        youtuber.add(subscriber2);

        youtuber.startLiveStreaming();
    }
}
```

**결과**\


<figure><img src="https://velog.velcdn.com/images/van1164/post/46fc1fae-957a-44ae-ba22-7540e6f64a62/image.png" alt=""><figcaption></figcaption></figure>
