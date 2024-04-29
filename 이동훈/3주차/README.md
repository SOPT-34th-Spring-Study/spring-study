# **스프링 핵심 원리 이해**2 - **객체 지향 원리 적용**

## 새로운 할인 정책

1. 기획자의 기획 변동 : 고정 금액 할인 → 금액의 10% 할인
2. RateDiscountPolicy 코드 추가하여 구현 클래스 변경
3. 테스트까지 완료(성공)

```java
   public class OrderServiceImpl implements OrderService {
 //    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
       private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
 }
```

클라이언트의 코드의 구현 클래스를 RateDiscountPolicy로 변경했지만 문제점 발생!

## 문제점

- 역할과 구현 분리는 OK
- 다형성 활용, 인터페이스와 구현객체 분리 OK
- **OCP, DIP 원칙 준수 X**

OCP : 요약하면 변경하지 않고 확장이 가능하다

**→ 지금 코드는 기능을 확장해서 변경하면, 클라이언트 코드에 영향을 줌**

DIP : **클라이언트( `OrderServiceImpl` )는 `DiscountPolicy` 인터페이스에 의존하면서 `FixDiscountPolicy`, `RateDiscountPolicy` 구체(구현) 클래스에도 의존**!

### 기대했던 의존관계

<img width="596" alt="001" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/53b9f276-9c33-4db9-aea8-98e04f773624">

but…

<img width="617" alt="002" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/618e1a18-3e46-4f9a-aab4-c3bdaf3239a3">

또한 여기서 FixDiscountPolicy를 RateDiscountPolicy로 변경하면 어떻게 될까?

<img width="604" alt="003" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/498ac494-d20c-436f-b32c-84e3e75f7bba">

그렇다. 클라이언트의 코드도 변경을 해주어야 한다.

### 문제 해결 방법 탐구

- DIP를 위반하므로 인터페이스에만 의존하도록 변경해보자!

다음과 같은 설계로 변경을 하면 DIP를 위반하지는 않는다.

<img width="599" alt="004" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/979b53fc-da7d-49c5-a978-9f6128ba6bf7">

```java
public class OrderServiceImpl implements OrderService {
     //private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
     private DiscountPolicy discountPolicy;
}
```

하지만 인터페이스에만 의존하도록 설계하면, 구현체가 없으므로 코드를 실행할 때 NPE(Null Pointer Exception)이 발생한다.

### 해결방안

**누군가 클라이언트(OrderServiceImpl)에 DiscountPolicy의 구현 객체를 대신 생성하고, 주입해주면 된다!!**

## 관심사의 분리

로미오와 줄리엣 공연을 하면 로미오 역할을 누가 할지 줄리엣 역할을 누가 할지는 배우들이 정하는게 아니다. 이 전 코드는 마치 로미오 역할(인터페이스)을 하는 레오나르도 디카프리오(구현체, 배우)가 줄리엣 역할(인터페이스)을 하는 여자 주인공(구현체, 배우)을 직접 초빙하는 것과 같다. 디카프리오는 공연도 해야하고 동시에 여자 주인공도 공연에 직접 초빙해야 하는 **다양한 책임**을 가지고 있다.

→ 배우는 본인의 역할인 배역을 수행하는 것에만 집중해야 하며, 공연을 구성하고, 담당 배우를 섭외하고, 역할에 맞는 배우를 지정하는  책임은 **공연기획자가** 해야한다.

공연기획자를 생성해보자!

### AppConfig의 등장

```java
 package hello.core;
 
 import hello.core.discount.FixDiscountPolicy;
 import hello.core.member.MemberService;
 import hello.core.member.MemberServiceImpl;
 import hello.core.member.MemoryMemberRepository;
 import hello.core.order.OrderService;
 import hello.core.order.OrderServiceImpl;
 
 public class AppConfig {
     public MemberService memberService() {
         return new MemberServiceImpl(new MemoryMemberRepository());
     }
     
     
     public OrderService orderService() {
         return new OrderServiceImpl(
             new MemoryMemberRepository(),
             new FixDiscountPolicy());
      }
}
```

- AppConfig는 애플리케이션의 실제 동작에 필요한 구현 객체를 생성한다.
    - MemberServiceImpl
    - MemoryMemberRepository
    - OrderServiceImpl
    - FixDiscountPolicy

- AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결) 해준다.
    - MemberServiceImpl → MemoryMemberRepository
    - OrderServiceImpl → MemoryMemberRepository , FixDiscountPolicy

### 비교

```java
public class MemberServiceImpl implements MemberService { 
	
	private final MemberRepository memberRepository = new MemoryMemerRepository();

}
```

```java
public class MemberServiceImpl implements MemberService {
 
	private final MemberRepository memberRepository;
	
	public MemberServiceImpl(MemberRepository memberRepository) {
         	this.memberRepository = memberRepository;
	}
}
```

기존 코드는 MemberServiceImpl이 MemberRepository와 MemoryMemberRepository 둘 다 의존했지만, 변경된 코드는 MemberRepository에만 의존하는 것을 볼 수 있다.

그러므로, MemberServiceImpl 입장에서 생성자를 통해 어떤 구현 객체가 주입될 지는 알 수 없다.

어떤 구현 객체를 주입할 지는 오직 외부(AppConfig)에서 결정된다/

결과적으로 MemberServiceImpl은 이제부터 의존관계에 대한 고민은 외부에 맡기고 실행에만 집중하면 된다.

<img width="617" alt="005" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/694fdbba-1f7d-4d60-a530-46c72ae500d8">

- 객체의 생성과 연결은 AppConfig가 담당
- **DIP 완성 :** MemberServiceImpl은 MemberRepository인 추상에만 의존하면 되고, 객체 생성은 외부(AppConfig)에서 알아서 해준다!!
- 관심사의 분리 : 객체를 생성하고 연결하는 역할과 실행하는 역할이 명확이 분리되었다.

### 다이어그램

<img width="628" alt="006" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/9b6d2f89-ec97-44c4-b674-531a10b20852">

```java
public class AppConfig {
	public MemberService memberService() {
            return new MemberServiceImpl(new MemoryMemerRepository());    
        }
}
```

코드와 같이 이해를 해보자.

AppConfig 객체는 MemoryMemberRepository 객체를 생성하고 그 참조값을 MemberServiceImpl의 생성자에 주입하고 나서 memberServiceImpl 객체를 반환한다.

memberServiceImpl 입장에서는 의존관계를 외부에서 주입해주는 것과 같다고 해서 DI(Dependency Injection) 우리말로 의존관계 주입 or 의존성 주입이라고 한다.

### 정리

- AppConfig를 통해서 관심사를 확실하게 분리했다.
- 배역, 배우를 생각해보자
- AppConfig는 공연 기획자이다.
- AppConfig는 구체 클래스를 선택한다. 배역에 맞는 담당 배우를 선택한다. 애플리케이션에 어떻게 동작해야 할 지 전체 구성을 책임진다.
- 이제 각 배우들은 담당 기능을 실행하는 책임만 지면 된다.
- OrderServiceImpl은 기능을 실행하는 책인만 지면 된다.

## AppConfig 리팩터링

기대하는 그림은 다음과 같다.

<img width="658" alt="007" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/958d81d6-46dc-401c-886c-d3d2b39431d8">

### 리팩터링 전

```java
 public class AppConfig {
 
     public MemberService memberService() {
         return new MemberServiceImpl(new MemoryMemberRepository());
     }
		 
     public OrderService orderService() {
         return new OrderServiceImpl(
                 new MemoryMemberRepository(),
                 new FixDiscountPolicy());
     }    
}
```

보면 new MemoryMemberRepository() 부분이 중복이 되었고, 역할과 구현이 한눈에 안들어온다. 역할과 구현이 한눈에 드러나게 리팩토링 해보자.

### 리팩터링 후

```java
public class AppConfig {

     public MemberService memberService() {
         return new MemberServiceImpl(memberRepository());
     }

     public OrderService orderService() {
         return new OrderServiceImpl(
                 memberRepository(),
                 discountPolicy());
     }
		 
     public MemberRepository memberRepository() {
         return new MemoryMemberRepository(); // 이 부분만 변경하면 됨
     }
		 
     public DiscountPolicy discountPolicy() {
         return new FixDiscountPolicy(); // 이 부분만 변경하면 됨.
     }
}
```

- `new MemoryMemberRepository()` 이 부분이 중복 제거되었다. 이제 `MemoryMemberRepository` 를 다른 구현체로 변경할 때 한 부분만 변경하면 된다.
- `AppConfig` 를 보면 역할과 구현 클래스가 한눈에 들어온다. 애플리케이션 전체 구성이 어떻게 되어있는지 빠르게 파악할 수 있다.

## 새로운 구조와 할인 정책 적용

처음으로 돌아가서 정액 할인 정책을 정률% 할인 정책으로 변경해보자

**AppConfig의 등장으로 애플리케이션이 크게 사용 영역과, 객체를 생성하고 구성하는 영역으로 분리되었다!**

<img width="606" alt="008" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/cc8ec8e0-0d92-406b-96b2-eda8f8b9376a">

<img width="634" alt="009" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/556b0664-1977-4877-8548-fc4539268e7c">

AppConfig에서 `return new FixDiscountPolicy()` → `return new RateDiscountPolicy()` 로 변경해도 구성 영역만 영향을 받고, 사용 영역은 전혀 영향을 받지 않는다.

**다시 한 번 강조하자면, 사용 영역은 코드 수정을 할 필요가 없고, 오로지 구성 영역의 코드만 수정하면 된다!**

**구성 영역은 당연히 변경된다.** 구성 역할을 담당하는 AppConfig를 애플리케이션이라는 공연의 기획자로 생각하자. **공연 기획자는 공연 참여자인 구현  객체들을 모두 알아야 한다.**



## 좋은 객체 지향 설계의 5가지 원칙의 적용(SOLID)

### SRP 단일 책임 원칙

한 클래스는 하나의 책임만 가져야 한다.

- 클라이언트 객체는 직접 구현 객체를 생성하고, 연결하고, 실행하는 다양한 책임을 가지고 있음 (너무 많은 일을 감당)
- SRP 단일 책임 원칙을 따르면서 관심사를 분리함
- 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당
- 클라이언트 객체는 실행하는 책임만 담당

### DIP 의존관계 역전 원칙

**프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” 의존성 주입은 이 원칙을 따르는 방법 중 하나**

- 새로운 할인 정책(정률 할인)을 개발해서 적용하려 하니 OrderServiceImpl(클라이언트) 코드도 변경해야 했음.

  → 왜냐하면 클라이언트는 DIP를 지키며 DiscountPolicy 추상화 인터페이스에 의존하는 것 처럼 보였으나, FixDiscountPolicy(구체화 클래스)에도 같이 의존했기 때문

- **그래서 AppConfig가 FixDiscountPolicy 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의존관계를 주입함. 이렇게 해서 DIP 원칙을 따르면서 문제해결**

### OCP 개방-폐쇄 원칙

**소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다**

- 다형성을 사용하고 클라이언트가 DIP를 지킴.
- **애플리케이션을 사용영역과 구성영역으로 나눔**
- **AppConfig가 의존관계를 FixDiscountPolicy → RateDiscountPolicy로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드에 변동 없음!!**
- **구성영역은 변동이 있어도 사용영역은 변동 없다!**

## IoC, DI, 그리고 컨테이너

### 제어의 역전

- 기존 프로그램은 클라이언트 구현 객체가 필요한 서버 구현 객체를 생성, 연결, 실행했다. 즉 구현 객체가 프로그램의 제어 흐름을 스스로 조종
- Appconfig가 등장한 이후에 구현 객체는 자신의 로직을 실행하는 역할만 담당함. 즉 프로그램의 제어 흐름의 주체가 AppConfig로 변경

  ex) OrderServiceImpl(클라이언트)는  필요한 인터페이스들을 호출하지만 어떤 구현 객체가 실행될 지 모름.

- 심지어 OrderServiceImpl도 AppConfig가 생성함. 그리고 AppConfig는 OrderServiceImpl이 아닌 OrderServcie 인터페이스의 다른 구현 객체를 생성하고 실행할 수도 있다. (그런 사실도 모른체 OrderServiceImpl은 묵묵히 자신의 로직을 실행할 뿐…)
- **이렇듯 프로그램의 제어 흐름을 직접 제어하는게 아니라 외부에서 관리하는 것을 제어의 역전(IoC)라고 한다.**

### 프레임워크 vs 라이브러리

- 프레임워크 : 내가 작성한 코드를 제어하고, 대신 실행 (ex. JUnit)
- 라이브러리 : 내가 작성한 코드가 직접 제어의 흐름을 담당

### 의존관계 주입 DI(Dependency Injection)

- OrderServiceImpl은 MemberRepository, DiscountPolicy 인터페이스에 의존한다. 어떤 구현객체가 사용될지는 모른다.
- **의존관계는 정적인 클래스 의존관계와, 실행시점에 결정되는 동적인 객체(인스턴스) 의존관계 둘을 분리해서 생각해야 한다.**

**정적인 클래스 의존관계**

- 클래스가 사용하는 import 코드만 보고 의존관계 쉽게 판단 가능.
- 정적인 의존관계는 애플리케이션을 실행하지 않아도 분석 가능

<img width="641" alt="010" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/f0a78d13-4a63-462a-b4ac-87f4d25139fe">

OrderServiceImpl은 MemberRepository, DiscountPolicy에 의존한다는 것을 알 수 있다.

**그런데 이러한 클래스 의존관계 만으로는 실제 어떤 객체가 OrderServiceImpl에 주입 될 지는 알 수 없다.**

**동적인 객체 인스턴스 의존 관계**

애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존 관계다.

<img width="616" alt="011" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/08de6f2a-de1f-44b7-8f24-cc32fe4f8ec5">

- 애플리케이션 실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실존 관계가 연결되는 것을 **의존관계 주입**이라고 한다.
- 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
- 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.
- **의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다. (ex. 메모리 회원 저장소로 할 지, 정액 할인 정책으로 할지)**

### IoC 컨테이너, Di 컨테이너

- AppConfig 처럼 객체를 생성하고 관리하면서 의존관게를 연결해 주는 것을 IoC 컨테이너, DI 컨테이너라고 함(의존관계에 초점을 두어 최근에는 주로 DI 컨테이노러 불림)
- 또는 어셈블러, 오브젝트 팩토리 등으로 불리기도 함.

## 스프링으로 전환하기
```java
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        MemberService memberService = new MemberServiceImpl();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find member = " + findMember.getName());
    }
}

```

```java
package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        MemberService memberService = new MemberServiceImpl();
//        OrderService orderService = appConfig.orderService();
//        OrderService orderService = new OrderServiceImpl();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 20000);

        System.out.println("order = " + order);
        System.out.println("order = " + order.calculatePrice());

    }
}

```
### 스프링 컨테이너

- `ApplicationContext`를 스프링 컨테이너라 한다.
- 기존에는 개발자가 `AppConfig`를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다.
- 스프링 컨테이너는 @Configuration이 붙은 AppConfig를 설정(구성) 정보로 사용한다. 여기서 @Bean이 붙은 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. (이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라고 함)
- 스프링 빈은 @Bean이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다. (memberService, orderService)
- 이전에는 개발자가 필요한 객체를 AppConfig를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾아야 한다. 스프링 빈은 applicationContext.getBean() 메서드를 사용해 찾을 수 있다.

### 스프링 부트 3.1 이상 - 로그 출력 방법

```java
<configuration>
     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
         <encoder>
             <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -%kvp-
%msg%n</pattern>
         </encoder>
     </appender>
  <root level="DEBUG">
         <appender-ref ref="STDOUT" />
	</root>
</configuration>
```

3.1 부터 기본 로그 레벨을 `INFO` 로 빠르게 설정하기 때문에 로그를 확인할 수 없는데, 이렇게하면 기본 로그 레벨을 `DEBUG` 로 설정해서 강의 내용과 같이 로그를 확인할 수 있다.
