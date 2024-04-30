**# 스프링 핵심 원리 - 기본편(섹션3)

---

### 🌱 섹션 3- 스프링 핵심 원리 이해2 객체 지향 원리 적용

> ***03-1. 새로운 할인 정책 개발***

할인 정책을 지금처럼 고정 금액 할인이 아닌 정률 할인 정책으로 변경하자. 고객 등급이 VIP인 경우 주문 금액의 10%를 할인해주자.
<br/>
<br/>


**RateDiscountPolicy 추가**

![https://blog.kakaocdn.net/dn/UJssb/btsAuM1oHvn/AJThB3qi8kfRPLd48jprKK/img.png](https://blog.kakaocdn.net/dn/UJssb/btsAuM1oHvn/AJThB3qi8kfRPLd48jprKK/img.png)
<br/>
<br/>

> ***03-2. 새로운 할인 정책 적용과 문제점***

할인 정책을 변경하려면 할인 정책의 클라이언트인 OrderServiceImpl의 코드를 고쳐야 한다.
<br/>

**`문제점 발견`**
✅ 역할과 구현을 분리했다. (O)

- DiscountPolicy (역할), FixDiscountPolicy(구현)

✅ 다형성을 활용하고, 인터페이스와 구현 객체를 분리했다. (O)
❌ DIP, OCP 등과 같은 객체 지향 설계 원칙을 준수했다. (X)

- DIP 위반
    - OrderServiceImpl은 추상(인터페이스)인 DiscountPolicy에 의존할 뿐만 아니라 구체(구현 클래스)인 FixDiscountPolicy, RateDiscountPolicy에도 의존하고 있다.
        
        ![https://velog.velcdn.com/images/julia2039/post/f6af7839-31fd-4ecc-ba5e-912265e83834/image.png](https://velog.velcdn.com/images/julia2039/post/f6af7839-31fd-4ecc-ba5e-912265e83834/image.png)
        
        ![https://blog.kakaocdn.net/dn/mf5GT/btsAFEPiq9l/y98PQKmZcqovYY0GmIWXZ1/img.png](https://blog.kakaocdn.net/dn/mf5GT/btsAFEPiq9l/y98PQKmZcqovYY0GmIWXZ1/img.png)
        

- OCP 위반
    - OCP기능을 확장해서 변경하면 클라이언트인 OrderServiceImpl 코드에 영향을 주기 때문에 OCP를 위반한다.
        - **지금 코드는 기능을 확장해서 변경하면, 클라이언트 코드에 영향을 준다!**** 따라서 ****OCP를 위반****한다.
        - OCP 원칙이란? `기존의 코드를 변경하지 않으면서, 기능을 추가할 수 있도록 설계`
          <br/>
          <br/>


**어떻게 해결 할 수 있을까?**

**인터페이스에만 의존하도록 설계를 변경하자!!**

 
DIP를 위반하지 않도록 클라이언트가 인터페이스에만 의존하도록 의존관계를 변경하면 된다.

설계 변경

![https://blog.kakaocdn.net/dn/bLqmPT/btsAFZxFl7V/FCkJluESYCATulkunmx8UK/img.png](https://blog.kakaocdn.net/dn/bLqmPT/btsAFZxFl7V/FCkJluESYCATulkunmx8UK/img.png)

	
 코드 변경

![https://blog.kakaocdn.net/dn/bd2J2E/btsAG19S1Ym/CoJcMZPOu5BOPsixrtifu0/img.png](https://blog.kakaocdn.net/dn/bd2J2E/btsAG19S1Ym/CoJcMZPOu5BOPsixrtifu0/img.png)

❗️NPE(Null Pointer Exception)이 발생

구현체가 없다… 따라서  *`int* discountPrice = discountPolicy.discount(member, itemPrice);`  *요 코드에서 NPE 발생*

**✔️ 해결방안**
이 문제를 해결하려면 누군가가 클라이언트인 `OrderServiceImpl` 에 `DiscountPolicy`의 구현 객체를 대
신 생성하고 주입해주어야 한다.
<br/>
<br/>

> ***03-3. 관심사의 분리***

애플리케이션을 하나의 공연이라고 생각하면, 로미오 역할을 누가할지, 줄리엣 역할을 누가할지는 배우들이 정하는 게 아니다!

이전 코드는 마치 로미오 역할(배역, 인터페이스)을 하는 레오나르도 디카프리오(배우, 구현체)가 줄리엣 역할(배역, 인터페이스)을 하는 여자 주인공(배우, 구현체)을 직접 초빙하는 것과 같다.

레오나르도 디카프리오는 배우로서 공연도 해야하고 동시에 여자 주인공도 공연에 직접 초빙해야 하는 다양한 책임을 가지고 있다.

`관심사를 분리하자`
 공연 기획자를 만들어서 배우와 공연 기획자의 책임을 확실히 분리하자.
<br/>
<br/>

### 🍀 AppConfig **등장**

애플리케이션의 전체 동작 방식을 구성(config)하기 위해, **`구현 객체를 생성`**하고, **연결**하는 책임을 가지는 별도의

설정 클래스를 만들자.

- AppConfig는 애플리케이션의 실제 동작에 필요한 **`구현 객체를 생성`** 하고, 생성한 객체 인스턴스의 참조(레퍼런스)를 **생성자를 통해서 주입(연결)**해준다.
    - `MemberServiceImpl, MemoryMemberRepository, OrderServiceImpl`
    - 주입을 이제 해줬으니까 아까처럼 `NPE` 발생 XX
    
    


- `OrderServiceImpl` 입장에서 생성자를 통해 어떤 구현 객체가 들어올지(주입될지)는 알 수 없다.
- `OrderServiceImpl` 의 생성자를 통해서 어떤 구현 객체을 주입할지는 오직 외부( `AppConfig` )에서 결정한
다.
- `OrderServiceImpl`은 이제부터 실행에만 집중하면 된다.

### **클래스 다이어그램**

![https://blog.kakaocdn.net/dn/ePIkzs/btsAFZyIC1g/ORHtQ7jntq0zFMeqzNgk4K/img.png](https://blog.kakaocdn.net/dn/ePIkzs/btsAFZyIC1g/ORHtQ7jntq0zFMeqzNgk4K/img.png)

객체의 생성과 연결은 AppConfig가 담당한다.

✔️ DIP 완성    - MemberServiceImpl은 MemberRepository인 추상에만 의존하면 된다. 이제 구체 클래스를 몰라도 된다.

✔️ 관심사의 분리    - 객체를 생성하고 연결하는 역할(AppConfig)과 실행하는 역할(MemberServiceImpl)이 명확히 분리되었다.

이제 다시 할인 정책 변경으로 ..!

**AppConfig의 등장으로 애플리케이션이 크게 사용 영역과, 객체를 생성하고 구성(Configuration)하는 영역으로 분리되었다.**
	

- Appcofig만 고치면 할인정책을 쉽게 변경할 수 있다! 클라이언트 코드인 `OrderServiceImpl` 를 포함해서 ****사용 영역****의 어떤 코드도 변경할 필요가 없다.
    
    ```java
        public DiscountPolicy discountPolicy() {
        
    		    //전
            return new FixDiscountPolicy();
            
            //후
            return new RateDiscountPolicy();
           
        }
    ```


<br/>
<br/>

> ***3-7.좋은 객체 지향 설계의** 5**가지 원칙의 적용***
여기서 3가지 SRP, DIP, OCP 적용
> 

1. SRP **단일 책임 원칙**

**: 한 클래스는 하나의 책임만 가져야 한다.**

- 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당
- 클라이언트 객체는 실행하는 책임만 담당

1. DIP **의존관계 역전 원칙**

**: 프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” → 의존성 주입은 이 원칙을 따르는 방법 중 하나다.**

- 클라이언트 코드가 `DiscountPolicy` 추상화 인터페이스에만 의존하도록 코드를 변경
- AppConfig가 `FixDiscountPolicy` 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의존관계를 주입했다. 이렇게해서 DIP 원칙을 따르면서 문제도 해결했다.

1. OCP 개방-폐쇄 원칙

**: 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다**

- 애플리케이션을 사용 영역과 구성 영역으로 나눔
- AppConfig가의존관계를 `FixDiscountPolicy`에서 `RateDiscountPolicy` 로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도 됨
- **소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀 있다!**
  <br/>
  <br/>

> ***03-8. Ioc, DI 그리고 컨테이너***
> 
🍀 IoC(Inversion Of Control) : 제어의 역전

: 구현 객체가 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것

기존 → 클라이언트가 스스로 필요한 서버 객체를 생성 (ex. `MemberSerivceImpl` 이 

`MemoryMemeberRepository`를 생성)

AppConfig 등장 후 → 프로그램의 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 구현객체(ex. OrderSeriviceImpl)는 자신의 로직만 실행하는 역할만 담당!

✔️ 프레임워크 vs 라이브러리

- 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. (JUnit)
- 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다.

### 🍀 DI: 의존관계 주입


- `OrderServiceImpl`은 `DiscountPolicy` 인터페이스에 의존한다. 실제 어떤 구현 객체가 사용될지는 모른다.
- 의존관계는 **정적인 클래스 의존 관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계** 둘을 분리해서 생각해야 한다.

---

### 🤔 더 알아보기 -  IoC와 DI에 대하여

> **🍀 IoC**
> 
> 
> **:객체의 생성, 생명주기의 관리까지 모든 객체에 대한 제어권이 바뀌었다는 것을 의미**
> 
> ### 스프링 컨테이너
> 
> **: 스프링 프레임워크에서 IoC 컨테이는 *스프링 컨테이너(`ApplicationContext)`* 이다.**
> 
> - 객체의 생성을 책임지고, 의존성을 관리
> - POJO의 생성, 초기화, 서비스, 소멸에 대한 권한을 가진다.
> - 개발자는 비즈니스 로직에 집중할 수 있다.
> 
> > **🌱 POJO란? 
> :** 주로 특정 자바 모델이나 기능, 프레임워크를 따르지 않는 Java Object를 지칭한다.
> `Java Bean` 객체가 대표적이다.
> ****
> 
> 
> ### IoC의 분류
> 
> DL(Dependency LookUp)과 DI(Dependency Injection)
> 
> 
> - DL: 저장소에 저장되어 있는 Bean에 접근하기 위해 컨테이너가 제공하는 API를 이용하여 Bean을 Lockup하는 것
> - DI: 각 클래스간의 의존관계를 빈 설정(Bean Definition) 정보를 바탕으로 컨테이너가 자동으로 연결해주는 것
>     - 생성자 주입, 수정자(setter) 주입, 필드 주입
> - DL 코드 살펴보기
>     
>     아래와 같이 Bean에 대한 정보가 있는 xml파일이 있다고 생각해보자.
>     
>     ```html
>     <beans>
>         <bean id="myObject" class="com.example.MyObject"/>
>     </beans>
>     ```
>     
>     java에서는 해당 xml의 Bean 정보들을 보고 어떤 클래스를 사용할지 검색하여 주입하게 된다.
>     
>     아래 자바코드를 살펴보자.
>     
>     ```java
>     String myConfigLocation = "classpath:myApplicationCTX.xml";
>     AbstractApplicationContext ctx = new GenericXmlApplicationContext(myConfigLocation);
>     MyObject myObject = ctx.getBean("myObject", MyObject.class);
>     ```
>     
>     그 결과 위와 같은 코드를 통해 적절한 MyObject클래스를 가져올 수 있는 것이다.
>     
> 
> 출처:
> 
> [https://dev-coco.tistory.com/80](https://dev-coco.tistory.com/80)
> 
> [슬기로운 개발생활:티스토리]
> 

### 정적인 클래스 의존 관계

- 애플리케이션을 실행하지 않아도 import 코드만 보고 판단 가능

  


클래스 다이어그램

### 동적인 객체 인스턴스 의존 관계

- 애플리케이션 **실행 시점(런타임)**에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것 👉 의존관계 주입
- ✨의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경 할 수 있다.
    - 위의 클래스 다이어그램이 전혀 바뀌지 않고, 아래의 객체 다이어그램만 바뀐다.


객체 다이어그램

### IoC **컨테이너**, DI **컨테이너**

- AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을 `IoC 컨테이너` 또는 **`DI 컨테이너`**라 한다.
- 의존관계 주입에 초점을 맞추어 최근에는 주로 DI 컨테이너라 한다.

> ***03-9. 스프링으로 전환하기***
> 
- AppConfig에 설정을 구성한다는 뜻의 `@Configuration` 을 붙여준다.
- 각 메서드에 `@Bean` 을 붙여준다. 이렇게 하면 스프링 컨테이너에 스프링 빈으로 등록한다.
- ![image](https://github.com/JungYoonShin/spring-study/assets/63058347/4084d0b6-f1d9-4424-a457-49764c4ae898)



### **스프링 컨테이너**

- `ApplicationContext`를 스프링 컨테이너라 한다.
- 기존에는 개발자가 `AppConfig` 를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다.
- 스프링 컨테이너는 `@Configuration`이 붙은 `AppConfig`를 설정(구성) 정보로 사용한다. 여기서 `@Bean`이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다.

---

### 🤔 더 알아보기 스프링 컨테이너 - 싱글톤 컨테이너에 대하여..

> **웹 어플리케이션과 싱글톤의 관계**
> 

![https://hongchangsub.com/content/images/2021/08/-----------2021-08-11------5.27.25.png](https://hongchangsub.com/content/images/2021/08/-----------2021-08-11------5.27.25.png)

여러 고객이 동시에 동일한 서비스를 요청하는 경우

- 통상적으로 서비스를 운영하다보면 위 그림과 같이 동일한 요청이 서로 다른 클라이언트로부터 동시에 들어올 수 있다.
- 요청이 들어오면 객체를 만들어서 메모리를 사용하게 되는데, 만약 동일한 요청들을 전부 상이한 메모리 공간에 할당시켜 각각 응답해주게되면 메모리 공간이 남아나질 않을 것이다.
- 이에 대한 해결방안으로 웹 어플리케이션을 구현할 때, 동일한 요청들에 대해서는 **싱글톤 패턴**을 적용시킨다.
    
    

> 싱글톤 패턴이란?
> 

: 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴

- 생성자가 여러 차례 호출되더라도 **실제로 생성되는 객체는 하나**이고 최초 생성 이후에 호출된 생성자는 **최초의 생성자가 생성한 객체를 재사용**
- 스프링은 기본적으로 별다른 설정을 하지 않으면 내부에서 생성하는 **빈 오브젝트를 모두 싱글톤으로 만든다**.

```java
public class Book {

    private static Book newBook = new NoteBook();

    public static Book getInstance()
    {
        return newBook;
    }

    private newBook(){};
}
```

![image](https://github.com/JungYoonShin/spring-study/assets/63058347/40ead5a8-61ea-4a5a-bfcf-621ab4f70bbf)


## **@Configuration과 싱글톤**

- `@Configuration`의 역할을 알아보기 위해, AppConfig 코드를 보자

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
        return new RateDiscountPolicy();
    }
}

```

- memberService 빈을 만드는 메서드를 호출하면 memberRepository()메서드를 호출한다.
- 위와 마찬가지로 orderService 빈을 만드는 메서드를 호출하면 동일하게 memberRepository()를 호출한다.
- memberRepository()는 new MemoryMemberRepository()를 호출한다.
- 결과적으로 서로 다른 new MemoryMemberRepository()가 생성되면서 싱글톤이 깨지는 것처럼 보인다.
- 하지만 당연히 스프링 컨테이너는 해당 인스턴스를 싱글톤 방식으로 처리한다.

![image](https://github.com/NOW-SOPT-SERVER/jsoonworld/assets/63058347/5aaae92a-29fb-47f5-8c3d-46f1e82228f7)
![image](https://github.com/JungYoonShin/spring-study/assets/63058347/11149de7-cf51-44e0-87da-bd662e9f36ad)
