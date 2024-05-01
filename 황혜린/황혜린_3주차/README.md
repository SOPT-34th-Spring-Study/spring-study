# 서버의 봄 3주차

# 섹션3

### **새로운 할인 정책 개발**

이 섹션에서는 고정 금액 할인에서 주문 금액의 비율로 할인하는 새로운 정률 할인 정책을 도입하는 것을 논의한다. 이 변화는 스프링의 객체 지향 원칙을 활용하여 보다 유연한 할인 방식으로 전환하는 것을 예시로 든다.

**코드 예시:**

```java
public class RateDiscountPolicy implements DiscountPolicy {
    private int discountPercent = 10; // 할인 비율 10%

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
```

이 클래스는 **`DiscountPolicy`** 인터페이스를 구현하며 VIP 회원에게 주문 금액의 10%를 할인한다.

### **AppConfig 리팩터링**

**`AppConfig`**는 스프링의 설정 파일로, 시스템의 전체 구성을 관리한한다. 리팩터링을 통해 중복을 제거하고 각 구성 요소가 하나의 역할만을 수행하도록 개선한다.

**리팩터링 전 코드:**

```java
@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(new MemoryMemberRepository(), new RateDiscountPolicy());
    }
}
```

**리팩터링 후 코드:**

```java
@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
```

이 설정은 각 서비스에 필요한 의존성을 주입하며 할인 정책을 쉽게 변경할 수 있는 유연한 구조를 제공한다.

### **관심사의 분리**

로미오와 줄리엣의 배역을 예로 들어 설명하며, 각 클래스가 자신의 역할에만 집중할 수 있도록 설계해야 함을 강조한다. 이는 **`AppConfig`**가 의존성을 주입하는 방식을 통해 달성된다. 이 구조는 개별 클래스가 의존성 해결과 객체의 생명주기 관리에서 해방되어, 각자의 핵심 비즈니스 로직 실행에만 집중할 수 있도록 한다.

### **DIP(의존관계 역전 원칙) 및 OCP(개방-폐쇄 원칙)**

새로운 할인 정책의 적용은 기존 코드를 변경하지 않고도 확장을 가능하게 해야 하며, 이는 DIP와 OCP를 준수함으로써 달성된다. **`OrderServiceImpl`**의 예에서 볼 수 있듯, **`AppConfig`**를 통해 의존성을 외부에서 주입받음으로써 높은 유연성과 확장성을 보장받을 수 있다. 이를 통해 애플리케이션은 새로운 요구 사항이나 변경 사항에 유연하게 대응할 수 있다.

### **IoC, DI, 그리고 컨테이너**

IoC(제어의 역전)는 스프링 컨테이너가 객체의 생명주기와 의존성을 관리한다는 원칙을 설명한한다. DI(의존성 주입)는 이러한 의존성 관리가 구체적으로 어떻게 이루어지는지를 설명하며, 이 과정에서 **`AppConfig`**는 중심 역할을 하며, 각 컴포넌트는 스프링 컨테이너에 의해 자동으로 연결된다. 이 시스템을 통해 개발자는 코드의 결합도를 낮추고, 유지보수성을 향상시킬 수 있다.

```java
@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
```

위 코드는 **`AppConfig`**에서 각종 서비스와 리포지토리, 할인 정책을 설정하는 방법을 보여준다. 이 설정은 스프링 컨테이너에 의해 관리되며, 각 컴포넌트 간의 의존성은 스프링 컨테이너가 자동으로 처리한다. 이렇게 함으로써 애플리케이션의 설정과 실행 로직이 명확히 분리되어, 각 부분이 자신의 역할에 충실할 수 있다.

# 섹션4

## **스프링 컨테이너와 스프링 빈**

스프링 컨테이너는 **`ApplicationContext`**를 사용하여 스프링 빈(객체)을 생성, 관리, 조작하는 기능을 제공한다. 이 과정에서 사용되는 설정 정보는 **`@Configuration`**이 붙은 자바 클래스 또는 XML 파일일 수 있다.

### **스프링 컨테이너 생성**

스프링 컨테이너를 생성하는 방법은 간단하다. 자바 설정 파일을 사용하는 경우, **`AnnotationConfigApplicationContext`**를 사용하여 컨테이너를 초기화할 수 있다.

**예제 코드:**

```java
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

이 코드는 **`AppConfig.class`**를 설정 정보로 사용하여 스프링 컨테이너를 생성한다.

### **스프링 빈 등록과 의존관계 설정**

스프링 컨테이너는 설정 클래스에서 **`@Bean`** 어노테이션이 붙은 메서드를 모두 호출하여 반환된 객체를 스프링 빈으로 등록한다. 빈의 이름은 기본적으로 메서드 이름을 사용하며, 필요에 따라 **`name`** 속성을 통해 명시적으로 이름을 부여할 수 있다.

**예제 코드:**

```java
@Bean(name="memberService2")
public MemberService memberService() {
    return new MemberServiceImpl(memberRepository());
}
```

이 코드는 사용자 정의 이름을 빈에 부여하여 스프링 컨테이너에 등록하는 예를 보여준다.

### **스프링 빈 조회**

스프링 컨테이너에서는 빈의 이름이나 타입으로 스프링 빈을 조회할 수 있다. 타입으로 조회할 때 동일한 타입의 빈이 여러 개 있으면, **`NoUniqueBeanDefinitionException`**이 발생할 수 있으며, 이 경우 빈의 이름을 지정해야 한다.

**예제 코드:**

```java
@Test
public void findBeanByName() {
    MemberService memberService = ac.getBean("memberService", MemberService.class);
    assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
}
```

이 테스트 코드는 이름으로 스프링 빈을 조회하는 방법을 보여준다.

### **스프링 빈 설정 메타 정보 - BeanDefinition**

스프링은 다양한 설정 형식을 지원하기 위해 **`BeanDefinition`**이라는 추상화를 사용한다. 이 추상화를 통해 XML 또는 자바 코드 등 다양한 형식의 설정 정보를 스프링 컨테이너가 처리할 수 있다.

**예제 코드:**

```java
GenericXmlApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
MemberService memberService = ac.getBean("memberService", MemberService.class);

```

이 코드는 XML 설정 파일을 사용하여 스프링 컨테이너를 초기화하고 빈을 조회하는 방법을 보여준다.