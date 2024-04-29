# 스프링 컨테이너와 스프링 빈

## 스프링 컨테이너 생성

```java
// 스프링 컨테이너 생성
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

- ApplicationContext를 스프링 컨테이너라고 한다.
- ApplicationContext는 인터페이스이다.
- 스프링 컨테이너 만드는 방법 2가지
    - XML 기반 방식
    - **애노테이션 기반 자바 설정 클래스 방식(AppConfig)**

- **※** 참고

  더 정확히는 스프링 컨테이너를 부를 때, ‘BeanFactory’와 ‘ApplicationContext’로 구분해서 이야기한다.

  최상위에 BeanFactory가 있고, 하위에 ApplicationContext가 있다.

  **보통 BeanFactory를 직접 사용하는 경우가 없기 때문에, 일반적으로 ApplicationContext를 스프링 컨테이너라고 한다.**


## 스프링 컨테이너의 생성 과정

### 1. 스프링 컨테이너 생성

<img width="767" alt="a1" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/8e060ec7-9ca4-4a37-b123-28e5f39a9f1f">

- new AnnotationConfigApplicationContext(AppConfig.class)로 스프링 컨테이너를 만듬
- **스프링 컨테이너 안에 스프링 빈 저장소가 있음. Bean 이름은 key, Bean 객체는 value**
- 여기서는 AppConfig를 구성 정보로 지정하였고, 스프링 컨테이너가 만들어지면 컨테이너가 AppConfig의 구성 정보 활용을 시작함.

### 2. 스프링 빈 등록

<img width="771" alt="a2" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/06a2dff5-9cfe-4ab4-ba81-9e62dc5abe97">

- **@Bean이 붙은 거를 모두 호출**
- 빈 이름은 보통 메서드 이름을 쓰는데, `@Bean(name="memberService2")` 와 같이 직접 이름을 부여할 수도 있다.
- **주의!**

  빈 이름은 항상 다른 이름을 부여해야 함! (무시 or 기존 빈 덮어버리는 오류 발생)



### 3. 스프링 빈 의존관계 설정 - 준비 및 완료

<img width="813" alt="a3" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/467e2210-55fc-4a5b-b7e8-6232c39e8b5b">

- **스프링 컨테이너는 설정 정보를 참고해 의존관계 주입(DI)**
- 이거에 대한 자세한 부분은 싱글톤 컨테이너에서 설명
- 참고

  스프링은 빈 생성과 의존관계 주입 단계가 나누어져 있음.

  근데 이렇게 자바코드로(@Bean) 빈을 등록하면 생성자를 호출하면서 의존관계 주입도 한번에 처리됨!!


## 컨테이너에 등록된 모든 빈 조회

```java
import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    @Test
    @DisplayName("모든 빈 출력하기") void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name=" + beanDefinitionName + " object=" +
                    bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기") void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
            //Role ROLE_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name=" + beanDefinitionName + " object=" + bean);
            }
        }
    }
}
```

- 모든 빈 출력(findAllBean)
    - ac.getBeanDefinitionNames() : 스프링에 등록된 모든 빈 이름을 조회한다.
    - ac.getBean() : 빈 이름으로 빈 객체(인스턴스)를 조회한다.
- 애플리케이션 빈 출력(findApplicationBean)
    - 스프링 내부에서 사용하는 빈 제외, 내가 등록한 빈만 출력
    - 스프링이 내부에서 사용하는 빈은 getRole()로 구분 가능
    - `ROLE_APPLICATION` : 일반적으로 사용자가 정의한 빈
    - `ROLE_INFRASTRUCTURE` : 스프링이 내부에서 사용하는 빈

## 스프링 빈 조회 - 기본

```java
import hello.core.AppConfig;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationContextBasicFindTest {
    AnnotationConfigApplicationContext ac = new
            AnnotationConfigApplicationContext(AppConfig.class);
    @Test
    @DisplayName("빈 이름으로 조회") void findBeanByName() {
        MemberService memberService = ac.getBean("memberService",
                MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }
    @Test
    @DisplayName("이름 없이 타입만으로 조회") void findBeanByType() {
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }
    @Test
    @DisplayName("구체 타입으로 조회") void findBeanByName2() {
        MemberServiceImpl memberService = ac.getBean("memberService",
                MemberServiceImpl.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }
    @Test
    @DisplayName("빈 이름으로 조회X") void findBeanByNameX() {
        //ac.getBean("xxxxx", MemberService.class);
        assertThrows(NoSuchBeanDefinitionException.class, () ->
                ac.getBean("xxxxx", MemberService.class));
    }
}

```

### 스프링 컨테이너에서 스프링 빈 조회 방법

- ac.getBean(빈이름, 타입)
- ac.getBean(타입)
- **타입은 구체 타입(구체 클래스)도 가능하나, 구현이 아닌 역할에 의존해야 하기에 인터페이스를 사용하자!**
- 조회 대상 스프링 빈이 없으면 예외 발생
    - NoSuchBeanDefinitionException: No bean named 'xxxxx' available

## 스프링 빈 조회 - 동일한 타입이 둘 이상

```java
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextSameBeanFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 중복 오류가 발생한다")
    void findBeanByTypeDuplicate() {
        //MemberRepository bean = ac.getBean(MemberRepository.class);
        assertThrows(NoUniqueBeanDefinitionException.class, () ->
                ac.getBean(MemberRepository.class));
    }

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 빈 이름을 지정하면 된다")
    void findBeanByName() {
        MemberRepository memberRepository = ac.getBean("memberRepository1",
                MemberRepository.class);
        assertThat(memberRepository).isInstanceOf(MemberRepository.class);
    }

    @Test
    @DisplayName("특정 타입을 모두 조회하기") void findAllBeanByType() {
        Map<String, MemberRepository> beansOfType =
                ac.getBeansOfType(MemberRepository.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value = " +
                    beansOfType.get(key));
        }
        System.out.println("beansOfType = " + beansOfType);
        assertThat(beansOfType.size()).isEqualTo(2);
    }

    @Configuration
    static class SameBeanConfig {
        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }
        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }
    }
}

```

- 타입으로 조회시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생하는데, 간단하다! 이때는 빈 이름을 지정하면 된다!
- `ac.getBeansOfType()` 을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.

## 스프링 빈 조회 - 상속 관계

```java
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextExtendsFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상 있으면, 중복 오류가 발생한다")
    void findBeanByParentTypeDuplicate() {
        //DiscountPolicy bean = ac.getBean(DiscountPolicy.class);
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(DiscountPolicy.class));
    }

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상 있으면, 빈 이름을 지정하면 된다")
    void findBeanByParentTypeBeanName() {
        DiscountPolicy rateDiscountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("특정 하위 타입으로 조회") void findBeanBySubType() { // 안좋은 방법
        RateDiscountPolicy bean = ac.getBean(RateDiscountPolicy.class);
        assertThat(bean).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회하기") void findAllBeanByParentType() {
        Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);
        assertThat(beansOfType.size()).isEqualTo(2);
        for (String key : beansOfType.keySet()) {System.out.println("key = " + key + " value=" +
                beansOfType.get(key));
        }
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회하기 - Object") void findAllBeanByObjectType() {
        Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key = " + key + " value=" + beansOfType.get(key));
        }
    }
    
    @Configuration
    static class TestConfig {
        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }
        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }
    }
}
```

<img width="781" alt="b" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/cf18ede9-f42a-4b4f-9481-355214a3db23">
- **자식타입은 다 끌려나온다 - 대원칙!**
- 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
- 따라서, Object 타입으로 조회하면, 모든 스프링 빈을 조회한다.

## BeanFactory와 ApplicationContext

<img width="399" alt="a4" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/c16d3ba4-4681-4182-bc89-46223a1fb3dd">

- **BeanFactory**
    - 스프링 컨테이너의 최상위 인터페이스
    - 스프링 빈을 관리하고 조회하는 역할을 담당
    - getBean()을 제공
    - 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능

- **ApplicationContext**
    - BeanFactory 기능을 모두 상속받아서 제공
    - **빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 그러면 둘의 차이는 무엇일까? → ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공**
    - 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.


### ApplicationContext가 제공하는 부가기능

<img width="781" alt="a5" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/0b4691d9-3fd8-47ba-b476-a0b62bbc118d">

- **메시지소스를 활용한 국제화 기능**
    - 예를 들어서 한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력
- **환경변수**
    - 로컬, 개발, 운영등을 구분해서 처리
- **애플리케이션 이벤트**
    - 이벤트를 발행하고 구독하는 모델을 편리하게 지원
- **편리한 리소스 조회**
    - 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회


### 요약

- ApplicationContext는 BeanFactory의 기능을 상속받음
- **ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공**
- BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용
- **BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다**

## 다양한 설정 형식 지원 - 자바 코드, XML

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memberService" class="hello.core.member.MemberServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
    </bean>
    <bean id="memberRepository"
          class="hello.core.member.MemoryMemberRepository" />
    <bean id="orderService" class="hello.core.order.OrderServiceImpl">
        <constructor-arg name="memberRepository" ref="memberRepository" />
        <constructor-arg name="discountPolicy" ref="discountPolicy" />
    </bean>
    <bean id="discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
</beans>
```

```java
import hello.core.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class XmlAppContext {
    @Test
    void xmlAppContext() {
        ApplicationContext ac = new
                GenericXmlApplicationContext("appConfig.xml");
        MemberService memberService = ac.getBean("memberService",
                MemberService.class);
        assertThat(memberService).isInstanceOf(MemberService.class);
    }
}

```

<img width="805" alt="a7" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/9a66398f-b67a-4457-b1de-0b879780d973">

### **애노테이션 기반 자바 코드 설정 사용**

- 지금까지 했던 것이다.
- `new AnnotationConfigApplicationContext(AppConfig.class)`
- AnnotationConfigApplicationContext 클래스를 사용하면서 자바 코드로된 설정 정보를 넘기면 된다.

### XML 설정 사용

- GenericXmlApplicationContext를 사용하면서 xml 설정 파일을 넘기면 된다.
- 스프링부트를 많이 사용하면서 지금은 잘 사용하지 않음.

## 스프링 빈 설정 메타 정보 - BeanDefinition

```java
import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanDefinitionTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    //    GenericXmlApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
    @Test
    @DisplayName("빈 설정 메타정보 확인") void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
            
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                System.out.println("beanDefinitionName" + beanDefinitionName + " beanDefinition = " + beanDefinition);
            }
        }
    }
}
```

<img width="805" alt="a7" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/9a66398f-b67a-4457-b1de-0b879780d973">
- **스프링 컨테이너는 메타정보를 기반으로 스프링 빈을 생성한다.**
- BeanDefinition을 빈 설정 메타정보라 한다.
    - @Bean , <bean> 당 각각 하나씩 메타 정보가 생성된다.
- **역할과 구현을 개념적으로 나눈 것으로, 다 몰라도 오직 BeanDefinition만 알면 된다.**

<img width="807" alt="a8" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/035c1c8d-9aab-4ef8-916e-dd0b06e05080">

- AnnotationConfigApplicationContext 는 AnnotatedBeanDefinitionReader를 사용해서
  AppConfig.class를 읽고 BeanDefinition을 생성한다.
- GenericXmlApplicationContext` 는 XmlBeanDefinitionReader를 사용해서 appConfig.xml설정 정보를 읽고 BeanDefinition을 생성한다.
- 새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 BeanDefinition을 생성하면 된다.