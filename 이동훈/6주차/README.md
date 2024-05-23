# 컴포넌트  스캔

## 컴포넌트 스캔과 의존관계 자동 주입 시작하기

- 지금까지 스프링 빈을 등록할 때는 자바 코드의 @Bean이나 XML의 <bean> 등을 통해서 설정 정보에 직접 등록할 스프링 빈을 나열했다.
- 예제에서는 몇개가 안되었지만, 이렇게 등록해야 할 스프링 빈이 수십, 수백개가 되면 일일이 등록하기도 귀찮고, 설정 정보도 커지고, 누락하는 문제도 발생한다.
- 그래서 스프링은 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 컴포넌트 스캔이라는 기능을 제공한다.
- 또 의존관계도 자동으로 주입하는 `@Autowired` 라는 기능도 제공한다.

```java
// 기존 코드
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
    //  return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}

// ComponentScan으로 Conponent 어노테이션이 붙은 클래스를 스프링 빈으로 등록
@Configuration
@ComponentScan(excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
public class AutoAppConfig {
}
```

- 컴포넌트 스캔을 사용하려면 먼저 @ComponentScan을 설정 정보에 붙여주면 된다.
    - 여기서 excludeFilters를 이용해 컴포넌트 스캔 대상에서 제외할 대상을 선택할 수 있다.
- 컴포넌트 스캔은 이름 그대로 `@Component` 애노테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록한다.

<img width="777" alt="q1" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/147f0b05-751d-4c3f-9d86-a85bec7965a4">

- 이런식으로 스프링 빈으로 등록할 클래스 위에 `@Component` 애노테이션을 붙여주면 된다.
- 이전 AppConfig는 @Bean으로 직접 설정 정보를 작성하고, 의존관계도 직접 명시했다.
    - 하지만 이제는 이런 설정 정보 자체가 없기에 의존관계 주입도 클래스 안에서 해결해야 한다.
    - `**@Autowired`로 의존관계를 자동으로 주입해주면 된다!!**


### 컴포넌트 스캔 및 자동 의존관계 주입

<img width="791" alt="q2" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/6acc97bf-bc36-4013-a30f-f37e4d98c707">

- `@ComponentScan` 은 `@Component` 가 붙은 모든 클래스를 스프링 빈으로 등록한다.
- **이때 스프링 빈의 기본 이름은 클래스명을 사용하되 맨 앞글자만 소문자를 사용한다.**
    - **빈 이름 기본 전략 :** MemberServiceImpl 클래스 memberServiceImpl
    - **빈 이름 직접 지정 :** 만약 스프링 빈의 이름을 직접 지정하고 싶으면`@Component("memberService2")` 이런식으로 이름을 부여하면 된다.


<img width="825" alt="q3" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/296ee951-a0c5-4290-94df-e61ae34e5ec3">

- 생성자에 `@Autowired` 를 지정하면, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.
- 이때 기본 조회 전략은 타입이 같은 빈을 찾아서 주입한다.
    - `getBean(MemberRepository.class)` 와 동일하다고 이해하면 된다.

<img width="769" alt="q4" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/31413427-107a-4b82-94a4-450e34c673d4">

- 생성자에 마라미터가 많아도 다 찾아서 주입

## 탐색 위치와 기본 스캔 대상

모든 자바 클래스를 다 컴포넌트 스캔을 하면 시간이 오래걸린다. 그래서 꼭 필요한 위치부터 탐색하도록 시작위치를 지정해야 한다.

```java
package hello.core;

@Configuration
@ComponentScan(
        basePackages = "hello.core.member",
        basePackageClasses = AutoAppConfig.class,
        excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
public class AutoAppConfig {
}
```

- basePackages : 탐색할 패키지의 시작 위치를 지정한다. 이 패키지를 포함해서 하위 패키지를 모두 탐색한다.
    - 즉 여기서는 hello.core.member 패키지를 포함해 그 하위 패키지를 모두 탐색한다.
    - basePackages = {"hello.core", "hello.service"} 처럼 여러 시작 위치를 지정할 수도 잇다.
- basePackageClasses : 지정한 클래스의 패키지를 탐색 시작 위치로 지정한다
    - 즉 여기서는 AutoAppConfig 클래스의 패키지인 hello.core가 탐색 시작 위치가 된다.
    - 만약 지정하지 않으면 `@ComponentScan` 이 붙은 설정 정보 클래스의 패키지가 시작 위치가 된다.

### 권장 방법

- **패키지 위치를 지정하지 않고, 설정 정보 클래스의 위치를 프로젝트 최상단에 두는 것이다.**
- **최근 스프링부트도 이 방법을 기본으로 제공한다.**
- 패키지 예시
    - com.hello
    - com.hello.serivce
    - com.hello.repository


`**com.hello` 프로젝트 시작 루트, 여기에 AppConfig 같은 메인 설정 정보를 두고, @ComponentScan 애노테이션을 붙이고, `basePackages` 지정은 생략한다.**

- 이렇게 하면 `com.hello` 를 포함한 하위는 모두 자동으로 컴포넌트 스캔의 대상이 된다.
- 그리고 프로젝트 메인 설정 정보는 프로젝트를 대표하는 정보이기 때문에 프로젝트 시작 루트 위치에 두는 것이 좋다
- 참고로 스프링 부트를 사용하면 스프링 부트의 대표 시작 정보인**`@SpringBootApplication`를 이 프로젝트 시작 루트 위치에 두는 것이 관례이다. (그리고 이 설정안에 바로 `@ComponentScan` 이 들어있다!)**

### 컴포넌트 스캔 기본 대상

- `@Component` : 컴포넌트 스캔에서 사용
- `@Controller` : 스프링 MVC 컨트롤러에서 사용
- `@Service` : 스프링 비즈니스 로직에서 사용
- `@Repository` : 스프링 데이터 접근 계층에서 사용
- `@Configuration` : 스프링 설정 정보에서 사용

<img width="636" alt="q5" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/e479af70-fd64-40d7-ac5f-97e3bca2b823">

- 실제로 보면  `@Component` 를 포함하고 있는 것을 알 수 있다.

### 부가기능

- 컴포넌트 스캔의 용도 뿐만 아니라 다음 애노테이션이 있으면 스프링은 부가 기능을 수행한다.
- `@Controller` : 스프링 MVC 컨트롤러로 인식
- `@Repository` : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환해준다.
- `@Configuration` : 앞서 보았듯이 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가 처리를 한다.
- `@Service` : **사실  @Service 는 특별한 처리를 하지 않는다. 대신 개발자들이 핵심 비즈니스 로직이 여기에 있겠구나 라고 비즈니스 계층을 인식하는데 도움이 된다.**

## 필터

### **컴포넌트 스캔 대상에 추가할 애노테이션**

```java
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}
```

### **컴포넌트 스캔 대상에 제외할 애노테이션**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}
```

### **컴포넌트 스캔 대상에 추가할 클래스**

```java
@MyIncludeComponent
public class BeanA {
}
```

### **컴포넌트 스캔 대상에 제외할 클래스**

```java
@MyExcludeComponent
public class BeanB {
}
```

### **설정 정보와 전체 테스트 코드**

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.ComponentScan.*;

public class ComponentFilterAppConfigTest {
    @Test
    void filterScan() {
        ApplicationContext ac = new
                AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);
        assertThat(beanA).isNotNull();
        Assertions.assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ac.getBean("beanB", BeanB.class));
    }
    @Configuration
    @ComponentScan(includeFilters = @Filter(type = FilterType.ANNOTATION, classes =
            MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION, classes =
                    MyExcludeComponent.class)
    )
    static class ComponentFilterAppConfig {
    }
}
```

```java
@ComponentScan(includeFilters = @Filter(type = FilterType.ANNOTATION, classes =
        MyIncludeComponent.class),
        excludeFilters = @Filter(type = FilterType.ANNOTATION, classes =
                MyExcludeComponent.class)
)
```

- `includeFilters` 에 `MyIncludeComponent` 애노테이션을 추가해서 BeanA가 스프링 빈에 등록된다.
- `excludeFilters` 에 `MyExcludeComponent` 애노테이션을 추가해서 BeanB는 스프링 빈에 등록되지 않는다.

### FilterType 옵션

- `ANNOTATION`: 기본값, 애노테이션을 인식해서 동작한다.
  ex) org.example.SomeAnnotation
- `ASSIGNABLE_TYPE`: 지정한 타입과 자식 타입을 인식해서 동작한다.
  ex) org.example.SomeClass
- `ASPECTJ`: AspectJ 패턴 사용
  ex) org.example..*Service+
- `REGEX`: 정규 표현식
  ex) org\.example\.Default.*
- CUSTOM: `TypeFilter` 이라는 인터페이스를 구현해서 처리
  ex) org.example.MyTypeFilter

### 만약 BeanA도 빼고 싶다면?

```java
@ComponentScan(
   includeFilters = {
         @Filter(type = FilterType.ANNOTATION, classes =
 MyIncludeComponent.class),
   },
   excludeFilters = {
         @Filter(type = FilterType.ANNOTATION, classes =
 MyExcludeComponent.class),
         @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BeanA.class)
   }
)
```

- 최근 스프링 부트는 컴포넌트 스캔을 기본으로 제공하는데, 개인적으로는 옵션을 변경하면서 사용하기 보다 는 스프링의 기본 설정에 최대한 맞추어 사용하는 것을 권장
- 참고

  @Component` 면 충분하기 때문에, `includeFilters`를 사용할 일은 거의 없다. `excludeFilters` 는 여러가지 이유로 간혹 사용할 때가 있지만 많지는 않다.



## 중복 등록과 충돌

컴포넌트 스캔에서 같은 빈 이름을 등록할 경우 충돌 발생

1. 자동 빈 등록 vs 자동 빈 등록
2. 수동 빈 등록 vs 자동 빈 등록

### 자동 빈 등록 vs 자동 빈 등록

- 컴포넌트 스캔에 의해 자동으로 스프링 빈이 등록되는데, 그 이름이 같은 경우 스프링은 오류를 발생시킨다.
    - `ConflictingBeanDefinitionException` 예외 발생

### 수동 빈 등록 vs 자동 빈 등록

```java
 @Component
 public class MemoryMemberRepository implements MemberRepository {}
```

```java
 @Configuration
 @ComponentScan(
         excludeFilters = @Filter(type = FilterType.ANNOTATION, classes =
 Configuration.class)
 )
 public class AutoAppConfig {
     @Bean(name = "memoryMemberRepository")
     public MemberRepository memberRepository() {
         return new MemoryMemberRepository();
     }
}
```

- 컴포넌트 어노테이션으로 스프링 빈으로 등록된 클래스는 앞글자만 소문자로 바뀌고 등록이 된다 → 즉, memoryMemberRepository라는 이름으로MemoryMemberRepository 클래스의 객체가 등록이 됨
- 그런데 AutoAppConfig 클래스에서 new MemoryMemberRepository()를 생성하는 메서드인 memberRepository()를 @Bean 어노테이션으로 등록해 놓았고 이름이 중복이다.
- **이 경우 수동 빈 등록이 우선권을 가진다. (수동 빈이 자동 빈을 오버라이딩 해버린다.)**

### 스프링부트 기본 설정

- 물론 개발자가 의도적으로 이런 결과를 기대했다면, 자동보다는 수동이 우선권을 가지는 것이 좋다.
- 현실은 개발자가 의도적으로 설정해서 이런 결과가 만들어지기 보다는 여러 설정들이 꼬여서 이런 결과가 만들어지는 경우가 대부분
- **따라서 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본 값을 바꾸었다.**
    - **스프링 부트에서도 spring.main.allow-bean-definition-overriding=true로 에러가 나지 않고, 수동 빈 등록이 우선권을 가지도록 설정할 수 있다.**