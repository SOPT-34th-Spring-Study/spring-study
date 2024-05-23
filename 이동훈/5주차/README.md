# 싱글톤 컨테이너

## 웹 애플리케이션과 싱글톤

- 스프링은 태생이 기업용 온라인 서비스를 지원하기 위해 탄생함.
- 대부분의 스프링 애플리케이션은 웹 애플리케이션이다. 물론 웹이 아닌 애플리케이션 개발도 얼마든지 개발할 수 있다. (ex. 배치 애플리케이션, 데몬 애플리케이션)
- 웹 애플리케이션은 보통 여러 고객이 동시에 요청을 한다.

<img width="793" alt="1" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/37800d49-4695-42a8-95fd-a26abb9631e7">

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
```

- 여기서 문제가 되는 점은 고객이 memberService를 요청할 때마다 `new MemberServiceImpl(memberRepository())`를 통해 객체가 계속 생성이 된다는 것이다!
    - 즉 고객이 100번 요청하면 100번의 객체가 만들어짐
    - 객체를 재사용해야 하는데, 메모리 낭비가 되는 상태!!

- 우리가 만들었던 스프링 없는 순수한 DI 컨테이너인 AppConfig는 요청을 할 때 마다 객체를 새로 생성한다.
    - 근데 트래픽이 초당 100이 나오면? 초당 100개 객체가 생성되고 소멸되게 된다. 즉 메모리가 쓸데없이 낭비되는 현상이 발생한다.
    - 따라서 객체가 딱 1개만 생성되고, 이를 공유하도록 설계를 해야한다. → **싱글톤 패턴**

## 싱글톤 패턴

**요약 : 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴**

```java
public class SingletonService {

    // static 영역에 객체를 딱 1개만 생성해둔다.
    private static final SingletonService instance = new SingletonService();

    // public으로 열어서 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한 다.
    public static SingletonService getInstance() {
        return instance;
    }

    // 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다.
    private SingletonService() {
    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
```

- static 영역에 객체 instance를 미리 하나 생성해서 올려둔다.
- 이제 객체 인스턴스가 필요하면 오로지 getInstance()를 호출해서만 조회할 수 있다. (항상 같은 instance만 반환)
- 외부에서 new 키워드로 객체 인스턴스를 생성할 수 있기에 생성자를 private로 막아준다.
- 생성하는데 드는 비용이 1000정도라면, 참조로 가져오는 비용은 1이라고 보면 됨.

```java
public class singletonTest {

    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너") void pureContainer() {
        AppConfig appConfig = new AppConfig();
        //1. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();
        //2. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();
        //참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1); System.out.println("memberService2 = " + memberService2);
        //memberService1 != memberService2
        assertThat(memberService1).isNotSameAs(memberService2);
    }

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    void singletonServiceTest() {
        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        //참조값이 같은 것을 확인
        System.out.println("singletonService1 = " + singletonService1);
        System.out.println("singletonService2 = " + singletonService2);

        // singletonService1 == singletonService2
        assertThat(singletonService1).isSameAs(singletonService2);

        singletonService1.logic();
    }
}
```

- 싱글톤을 적용하지 않은 순수한 DI 컨테이너는 호출할 때마다 객체를 생성하기에 객체 참조값이 다르다.
    - memberService1 ≠ memberService2
- **싱글톤 패턴을 적용하여 getInstance()로 동일한 객체를 가져온다. 따라서 객체의 참조값이 같다.**
    - memberService1 == memberService2


### **싱글톤 패턴 문제점**

하지만 싱글톤 패턴이 객체를 재사용 할 수 있다고 해서 항상 좋은 것만은 아니다.

문제점은 다음과 같다.

- 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
- **의존관계상 클라이언트가 구체 클래스에 의존한다. DIP를 위반한다. → 구체 클래스.getInstance()**
- 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다.
- 테스트하기 어렵다.
- 내부 속성을 변경하거나 초기화 하기 어렵다.
- private 생성자로 자식 클래스를 만들기 어렵다.
- 결론적으로 유연성이 떨어진다.
- 안티패턴으로 불리기도 한다.

**하지만 스프링부트는 위의 모든 단점을 해결해주고 객체 인스턴스를 싱글톤(1개만 생성)으로 관리할 수 있게 해준다!**

## 싱글톤 컨테이너

위에서도 말했듯이, 스프링 컨테이너는 싱글톤 패턴의 문제점을 해결하면서, 객체 인스턴스를 싱글톤(1개만 생성)으로 관리한다.

지금까지 우리가 학습한 **스프링 빈이 바로 싱글톤으로 관리되는 빈이다**.

### 싱글톤 컨테이너

<img width="1095" alt="2" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/76ccf4d8-59a4-4e1d-ba49-b19e85ba8705">

- 스프링 컨테이너는 싱글톤 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리한다.
    - **이전에 공부했던 스프링 컨테이너의 스프링 빈 등록과정을 자세히 보면, 컨테이너는 스프링 빈을 저장소에 하나만 등록해두는 것을 볼 수 있다!!**
- 이렇게 스프링 컨테이너는 싱글톤 컨테이너 역할을 하며, 이렇게 싱글톤 객체를 생성 및 관리하는 기능을 “**싱글톤 레지스트리**”라고 한다.
- 스프링 컨테이너의 이러한 기능 덕에 싱글톤 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다!!
    - **싱글톤 패턴을 위한 지저분(많은 사전작업) 코드가 들어가지 않아도 된다.**
    - **DIP, OCP, 테스트 private 생성자로 부터 자유롭게 싱글톤 사용 가능**


### 싱글톤 컨테이너 적용 후

<img width="770" alt="3" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/009d1674-671d-4e94-b044-328553a2de9e">

```java
@Test
@DisplayName("스프링 컨테이너와 싱글톤")
void springContainer() {
//        AppConfig appConfig = new AppConfig();
  ApplicationContext ac = new AnnotationConfigApplicationContext((AppConfig.class));
  MemberService memberService1 = ac.getBean("memberService", MemberService.class);
  MemberService memberService2 = ac.getBean("memberService", MemberService.class);

  //참조값이 같은 것을 확인
  System.out.println("memberService1 = " + memberService1); System.out.println("memberService2 = " + memberService2);
  //memberService1 != memberService2
  assertThat(memberService1).isSameAs(memberService2);
}
```

<img width="418" alt="a" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/bae35d6e-1344-4d93-8d83-b450532b5e4d">

- 코드 설명
    - **ApplicationContext**는 스프링 컨테이너를 의미한다.
    - **AnnotationConfigApplicationContext**는 주어진 클래스(**AppConfig.class**)의 설정 정보를 사용하여 스프링 컨테이너를 초기화한다
    - **AppConfig** 클래스는 스프링 빈을 선언하는 설정 클래스이다.
- 이렇게 스프링 컨테이너 객체를 생성하고, 등록된 스프링 빈을 getBean()으로 불러와서 비교해보면, memberSerivce1가 참조하는 객체와 memberService2가 참조하는 객체가 같다는 것을 확인할 수 있다.
- 따라서 스프링 컨테이너 덕분에 고객의 요청이 올 때 마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유해서 효율적으로 재사용할 수 있다.
- 참고

  스프링의 기본 빈 등록 방식은 싱글톤이지만, 싱글톤 방식만 지원하는 것은 아니다. 요청할 때 마다 새로운 객체를 생성해서 반환하는 기능도 제공한다. 자세한 내용은 뒤에 빈 스코프에서 설명


## (중요!)싱글톤 방식의 주의점

- **모든 싱글톤 방식(싱글톤 패턴, 스프링 같은 싱글톤 컨테이너 사용, 객체 인스턴스를 하나만 생성해서 공유하는 싱글톤 등)은 무상태(stateless)로 설계해야 한다!**
    - **여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문!!**
    - **스프링 빈의 필드에 공유 값을 설정하면 정말 큰 장애가 발생할 수 있다!**

### 무상태로 설계하라…?

- 특정 클라이언트에 의존적인 필드가 없어야 함
- 특정 클라이언트가 값을 변경할 수 있는 필드가 없어야 함
- 가급적 읽기만 가능해야 함
- 필드 대신 자바에서 공유되지 않는 지역변수, 파라미터, TreadLocal 등을 사용해야 함.

### Stateful한 설계

```java
public class StatefulService {

    private int price;

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
```

```java
public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        // statefulService1의 참조값 == statefulService2의 참조값
        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        //ThreadA: A사용자 10000원 주문
        statefulService1.order("userA", 10000);
        //ThreadB: B사용자 20000원 주문
        statefulService2.order("userB", 20000);

        //ThreadA: 사용자A 주문 금액 조회
        int price = statefulService1.getPrice();
        //ThreadA: 사용자A는 10000원을 기대했지만, 기대와 다르게 20000원 출력
        System.out.println("price = " + price);

        assertThat(statefulService1.getPrice()).isEqualTo(20000);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}
```

- **`StatefulService` 의 `price` 필드는 공유되는 필드인데, 특정 클라이언트가 값을 변경한다.**
- 사용자 A의 주문금액은 10000원이 되어야 하나, 20000원 이라는 결과가 나옴.
    - 이유는 싱글톤이므로 memberService1과 memberService2는 같은 객체를 참조하는데, private int price 필드를 공유 필드로 설계해 두었기 때문에 20000원이 나오는 것이다.
    - 당연히 같은 객체를 참조하는 참조변수들로 공유필드를 변경하기에 20000원이 나온다고 생각하면 된다.


### Stateless한 설계

```java
package hello.core.singleton;

public class StatefulService {

//    private int price;

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        return price;
    }
}
```

```java
public class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        // statefulService1의 참조값 == statefulService2의 참조값
        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);

        //ThreadA: A사용자 10000원 주문
        int userAPrice = statefulService1.order("userA", 10000);
        //ThreadB: B사용자 20000원 주문
        int userBPrice = statefulService2.order("userB", 20000);

        System.out.println("price = " + userAPrice);
        System.out.println("price = " + userBPrice);
    }

    static class TestConfig {

        @Bean
        public StatefulService statefulService() {
            return new StatefulService();
        }
    }
}
```

- price를 공유 필드가 아닌 매개변수로 관리하므로서, Stateless하게 설계하였다.

## @Configuration과 싱글톤

```java
 @Configuration
 public class AppConfig {
     @Bean
     public MemberService memberService() {
         return new MemberServiceImpl(memberRepository());
     }
     @Bean
     public OrderService orderService() {
         return new OrderServiceImpl(
                 memberRepository(),
                 discountPolicy());
     }
     @Bean
     public MemberRepository memberRepository() {
         return new MemoryMemberRepository();
     }
}
```

- memberService 빈을 만드는 코드를 보면 memberRepository()를 호출한다.
    - 또한 new MemoryMemberRepository()도 호출한다.
- orderService 빈을 만드는 코드도 memberRepository()를 호출한다.
    - 또한 new MemoryMemberRepository()도 호출한다.
- 마지막으로 memberRepository 빈을 만드는 코드는 직접 new MemoryMemberRepository()를 호출한다.

**결과적으로 new 키워드를 통해 3개의 다른 MemoryRepository 객체를 생성하여 싱글톤이 깨지는게 아닌가?**

- **결론적으로 말하면 아니다!**
- **스프링 컨테이너는 @Configuration을 통해 해당 문제를 해결한다!**

### 서로 다른 MemoryRepository 객체일까?

```java
public class ConfigurationSingletonTest {

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        // 모두 같은 인스턴스를 참조함
        System.out.println("memberService -> memberRepository = " + memberService.getMemberRepository());
        System.out.println("orderService -> memberRepository = " + orderService.getMemberRepository());
        System.out.println("memberRepository = " + memberRepository);

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);
    }
}

```

<img width="568" alt="k" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/4c95c3af-7265-4dfb-9dc1-d881d7206dab">

- **확인해보면 memberRepository가 참조하는 MemberRepository 객체의 주소는 모두 같다.**
- **즉 모두 같은 인스턴스를 공유해서 사용한다.**
- 근데 AppConfig의 자바 코드를 보면 분명 3번 new 키워드를 통해 MemoryMemberRepsitory를 호출하기에 다른 인스턴스가 생성이 되어야 한다.
- 그렇다면 총 세 번 호출이 안되는 것인지 실험을 통해 알아보자.

### memberRepository()가 3번 호출되는 지 실험

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
```

- 가정 : 스프링 컨테이너가 각각 @Bean을 호출해서 스프링 빈을 생성한다. 그래서 memberRepository() 는 다음과 같이 총 3번이 호출되어야 한다.
    - **스프링 컨테이너가 스프링 빈에 등록하기 위해 @Bean이 붙어있는`memberRepository()` 호출**
    - **memberService() 로직에서 `memberRepository()` 호출**
    - **orderService() 로직에서 `memberRepository()` 호출**
- 즉, 출력 예상은 다음과 같다.

  > call AppConfig.memberService
  call AppConfig.memberRepository
  call AppConfig.memberRepository
  call AppConfig.orderService
  call AppConfig.memberRepsitory>

- 하지만 실제 출력은 다음과 같다.

<img width="516" alt="k" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/f3321408-eac1-4a99-839e-f4b8e8e1b977">
  > call AppConfig.memberService
  call AppConfig.memberRepository
  call AppConfig.orderService
  >
    - **1번만 호출됨.**


## @Configuration과 바이트코드 조작의 마법

스프링 컨테이너는 싱글톤 레지스트리로서, 스프링 빈이 싱글톤이 되도록 보장해주어야 한다.

그런데 스프링이 자바 코드까지는 어떻게 하기 어렵다. 그러므로 분명 3번이 호출되어야 하는게 맞다. 하지만 그러면 싱글톤이 깨진다. 그러면 어떻게 한 번만 호출을 하는걸까?

**그 비밀은 @Configuration을 적용한 AppConfig에 있다**

```java
@Test
void configurationDeep() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    //AppConfig도 스프링 빈으로 등록된다.
    AppConfig bean = ac.getBean(AppConfig.class);
    System.out.println("bean = " + bean.getClass());
    //출력: bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70
}
```

- 사실 `AnnotationConfigApplicationContext` 에 파라미터로 넘긴 값은 스프링 빈으로 등록된다. 그래서`AppConfig` 도 스프링 빈이 된다.
- AppConfig 스프링 빈을 조회해 클래스 정보를 출력해보자

<img width="337" alt="10" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/2a4200ed-6fd7-435e-aa9b-d52bcbd0141d">

- 뭔가 이상하다. 기대하는 값은 `bean = class hello.core.AppConfig`
  였지만 클래스 명에 xxCGLIB가 붙어있다.
- **이것은 내가 만든 AppConfig 클래스가 아닌 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용해 AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고, 그 다른 클래스를 스프링 빈으로 등록한 것!**

### 구조도

<img width="789" alt="4" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/a57a70dd-c9f5-48d4-9a87-3d5be6cebd3e">

- 임의의 다른클래스 AppConfig@CGLIB가 싱글톤이 보장되도록 해준다.
- 실제로는 더 복잡하지만 다음과 같이 바이트 코드를 조작해서 작성이 되어있다.

<img width="732" alt="11" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/e7cd243a-944c-43d7-8947-b4556769327e">

- **memberRepository() 메서드를 AppConfig@CGLIB에서 오버라이딩 한다.**
- **@Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면, 존재하는 빈을 반환하고 없으면 스프링 빈으로 등록하고 반환하는 코드가 동적으로 만들어진다**.
- 덕분에 싱글톤이 보장되는 것이다.
- 참고

  AppConfig@CGLIB는 AppConfig의 자식 타입이므로, AppConfig 타입으로 조회 할 수 있다.


### @Configuration 없이 @Bean만 적용한다면?

**결론부터 말하자면 싱글톤을 보장하지 않는다!!**

주요 특징들은 다음과 같다.

1. **순수한 AppConfig로 스프링 빈에 등록된다.**

<img width="283" alt="22" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/b8f32785-47d7-49d0-b12f-013dcbe14714">

2. **MemberRepsitory가 총 3번 호출이 된다.**

<img width="516" alt="33" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/9ebd6a5e-103c-4f55-a02f-c1e9e20947b4">

    - 1번은 @Bean에 의해 스프링 컨테이너에 등록하기 위해, 나머지 2번은 memberRepository()를 호출하면서 발생한 코드
3. **각각 다른 MemoryMemberRepsitory 인스턴스를 갖게 된다.**

<img width="572" alt="44" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/65cbba83-6e4c-46ec-ac34-4cf521c1b078">

    - 주소 값이 다 다르다!!!

## 정리

- @Bean만 사용해도 스프링 빈으로 등록되지만, 싱글톤을 보장하지 않는다.
    - memberRepository()처럼 의존관계 주입이 필요해서 메서드를 직접 호출할 때 싱글톤을 보장하지 않는다.
- 크게 고민할 것이 없다. 스프링 설정 정보는 항상 `@Configuration` 을 사용하자.