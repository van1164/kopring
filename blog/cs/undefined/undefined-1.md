# 팩토리 패턴

### ✍ 팩토리 패턴 <a href="#undefined" id="undefined"></a>

팩토리 패턴은 객체 생성 부분을 떼어내서 추상화한 패턴으로 `new` 연산자를 통해 객체를 생성하는 것이 아니라 객체 생성을 맡을 팩토리 클래스를 만들고 이를 통해 객체를 만드는 방식이다. 특히나 하나의 상위 클래스가 있고 하위 클래스가 여러개 있을 때, 구체적인 클래스 생성을 팩토리가 맡도록 구현되어 생성과정에 유연성이 생긴다.

#### 💻코드 예시 <a href="#undefined" id="undefined"></a>

**팩토리**

```java
public class SimplePizzaFactory {

	public Pizza createPizza(String type) { 
		Pizza pizza = null;

		if (type.equals("cheese")) {
			pizza = new CheesePizza();
		} else if (type.equals("pepperoni")) {
			pizza = new PepperoniPizza();
		} else if (type.equals("clam")) {
			pizza = new ClamPizza();
		} else if (type.equals("veggie")) {
			pizza = new VeggiePizza();
		}
		return pizza;
	}
}
```

**클라이언트**

```java
public class PizzaStore {
	SimplePizzaFactory factory; 
 
	public PizzaStore(SimplePizzaFactory factory) {
		this.factory = factory;
	}
 
	public Pizza orderPizza(String type) {
		Pizza pizza;
 
		pizza = factory.createPizza(type);
 
		pizza.prepare();
		pizza.bake();
		pizza.cut();
		pizza.box();

		return pizza;
	}

}
```

위와 같이 클라이언트에서 Pizza를 생성할 때 직접 생성하는 것이 아닌 팩토리를 거쳐서 생성하게 된다. 객체 생성을 담당하는 클래스를 만들어 한곳에 관리하여 결합도를 낮추고 생성에 대한 수정사항은 팩토리만 확인하면 된다.

\
