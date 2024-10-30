# 전략 패턴

### ✍ 전략 패턴 <a href="#undefined" id="undefined"></a>

**전략 패턴**은 실행 중에 객체 동작을 실시간으로 바뀌도록 할 수 있게 하는 디자인 패턴이다. 객체의 행위를 바꾸고 싶은 경우 `직접 수정하지 않고` 전략이라고 부르는 `캡슐화한 알고리즘`을 바꿔주면서 상호 교체가 가능하게 만드는 패턴이다.

#### 💻코드 예시 <a href="#id-1" id="id-1"></a>

**무기 클래스**

```java
interface Weapon {
    void offensive();
}

class Sword implements Weapon {
    @Override
    public void offensive() {
        System.out.println("칼을 휘두르다");
    }
}

class Shield implements Weapon {
    @Override
    public void offensive() {
        System.out.println("방패로 밀친다");
    }
}

class CrossBow implements Weapon {
    @Override
    public void offensive() {
        System.out.println("석궁을 발사하다");
    }
}
```

**전략 패턴**

```java
class TakeWeaponStrategy {
    Weapon wp;

    void setWeapon(Weapon wp) {
        this.wp = wp;
    }

    void attack() {
        wp.offensive();
    }
}
```

**클라이언트**

```java
class User {
    public static void main(String[] args) {
        TakeWeaponStrategy hand = new TakeWeaponStrategy();

        hand.setWeapon(new Sword());
        hand.attack();

        hand.setWeapon(new Shield());
        hand.attack();

        hand.setWeapon(new Crossbow());
        hand.attack();
    }
}
```

위와 같이 클라이언트에서 사용하려는 무기를 전략 클래스의 변경을 통해 알고리즘을 변경할 수 있다. 이렇게 전략패턴을 사용하면 나중에 무기 종류가 새로 추가되어도 코드 수정없이 새로운 클래스를 생성만으로 무기를 추가할 수 있어서 유지 보수에 용이한 코드가 된다.

#### ❌ 전략 패턴이 아니었다면? <a href="#undefined" id="undefined"></a>

```java
class TakeWeapon {
    public static final int SWORD = 0;
    public static final int SHIELD = 1;
    public static final int CROSSBOW = 2;

    private int state;

    void setWeapon(int state) {
        this.state = state;
    }

    void attack() {
        if (state == SWORD) {
            System.out.println("칼을 휘두르다");
        } else if (state == SHIELD) {
            System.out.println("방패로 밀친다");
        } else if (state == CROSSBOW) {
            System.out.println("석궁을 발사하다");
        }
    }
}
```

전략패턴을 사용하지 않았다면 위와 같이 매 메소드마다 state를 넘겨 받아 무기를 선택해서 동작을 결정해야 하며 무기가 추가 된다면 메소드마다 코드를 추가해주어야 해서 유지보수가 매우 불편한 코드가 된다.

\
