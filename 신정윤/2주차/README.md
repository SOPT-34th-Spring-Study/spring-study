# 스프링 핵심 원리 - 기본편

---

## 🌱 섹션 1 - 객체 지향 설계와 스프링

### 👉 `2-1` 비즈니스 요구사항과 설계

> 회원, 주문과 할인 정책에서 어떤 요구사항을 충족해야하는지 설계한다.
물론! 처음 설계한 내용이 후에 바뀔 수 있다. 
그렇다고 무한정 기다릴 수 없으니 앞에서 배운 객체 지향 설계 방법으로 **`인터페이스`를 만들고 구현체를 언제든지 갈아끼울 수 있도록 설계**한다.
> 

### 1️⃣ 회원 도메인 설계

비즈니스에서 주요 도메인은 크게 회원, 주문과 할인 두가지로 구분하였다. 첫번째로 회원 도메인이다.

```
요구사항
- 회원 가입 및 조회
- 회원 등급은 일반과 VIP 두 등급 존재
- 회원 데이터는 자체 DB 구축 가능 혹은 외부 시스템과 연동 가능(미확정)
```

도메인의 요구사항을 정했으면, **도메인의 협력 관계, 클래스 다이어그램, 객체 다이어그램**을 결정한다.

- **도메인 협력 관계**
    
    > 🍀 기획자도 볼 수 있는 그림으로 요구사항 분석 과정에서 `소통 도구`로 사용된다.
    > 
    
    - `역할`: 클라이언트, 회원서비스, 회원저장소
    - `구현`: 메모리 회원 저장소, DB 회원 저장소, 외부 시스템 연동 회원 저장소 (**언제든지 구현체는 갈아끼울 수 있다!)**
    
    ![https://velog.velcdn.com/images/wnajsldkf/post/ae040466-6060-46a8-a877-35675c7442cb/image.png](https://velog.velcdn.com/images/wnajsldkf/post/ae040466-6060-46a8-a877-35675c7442cb/image.png)
    

- **클래스 다이어그램**
    
    > 🍀 도메인 다이어그램을 바탕으로 더 구체화하여 서버를 실제로 실행하지 않고 클래스의 의존 관계만 보고 그릴 수 있다.
    아래예시에서, MemoryMemberRepository를 넣을지, DbMemberRepository를 넣을지 서버가 실제로 실행될 때 정해지기 때문에, 클래스 다이어그램만으로는 모른다!
    > 

- `역할`: MemberService ↔ `구현`: MemberSerivceImpl
- `역할`: MemberRepository ↔ `구현`: MemoryMemberRepository, DbMemeberRepository

![https://velog.velcdn.com/images/wnajsldkf/post/a4f7837a-81f7-4282-b3c4-f9f37f69224e/image.png](https://velog.velcdn.com/images/wnajsldkf/post/a4f7837a-81f7-4282-b3c4-f9f37f69224e/image.png)

- **객체 다이어그램**
    
    > 🍀 객체들의 연관관계를 표현한 그림으로, 실제 서버를 띄웠을 때 생성한 인스턴스끼리의 참조관계를 표현한다.
    > 

- 클라이언트는 회원서비스(Impl)을 바라보고, 회원 서비스는 메모리 회원 저장소를 바라본다.

![https://velog.velcdn.com/images/wnajsldkf/post/61a63bfb-c07f-4bfc-a993-fa5d8f34d6e5/image.png](https://velog.velcdn.com/images/wnajsldkf/post/61a63bfb-c07f-4bfc-a993-fa5d8f34d6e5/image.png)

### 2️⃣ 회원 도메인 개발

- `member` 패키지를 생성

```
hello.core
ㄴ member
```

- Member 클래스 생성

회원 등급을 갖는 Enum 타입의 Grade를 생성하고 `Member` 클래스를 생성한다.

```java
public class Member {
    private Long id;
    private String name;
    private Grade grade;

    public Member(Long id, String name, Grade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }
    ...
}
```

- `MemberRepository` 인터페이스 생성

repository 패키지는 DB에 접근하는 코드의 모음이라고 생각하면 된다.

```java
package hello.core.member;

public interface MemberRepository {
    void save(Member member);

    Member findById(Long memberId);
}
```

- 구현체 클래스인 `MemoryMemberRepository`를 생성한다.

```java
package hello.core.member;

import java.util.HashMap;
import java.util.Map;

public class MemoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
```

`private static Map<Long, Member> store = new HashMap<>();` 
영한님 왈 > 위의 코드에서 HashMap은 동시성 이슈가 있으므로 실무에서는 Cocurrent HashMap을 쓰는 게 좋다고 하심!

### 🤔 Concurrent HashMap ?!

> 🍀  Key Value에 null을 허용하지 않으며 동기화를 보장한다.
> 
> 
> 동기화 처리 시, 조작하는 **버킷(index)에 대해서만 락**을 걸기 때문에, 같은 멀티 쓰레드 환경에서 사용하더라도 **HashTable 대비 속도가 빠르다.**
> 
> ![https://velog.velcdn.com/images/alsgus92/post/afa39653-8186-4d0d-a97b-06b8ae830b50/image.png](https://velog.velcdn.com/images/alsgus92/post/afa39653-8186-4d0d-a97b-06b8ae830b50/image.png)
> 

### 🤔 HashMap ?!

> 🍀Key Value에 null을 허용하지만, **동기화를 보장하지 않는다.**
> 
> 
> 동기화 처리를 하지 않기 때문에 데이터 탐색 속도는 빠르지만, 신뢰성과 안정성이 떨어진다.
> 
> ![https://velog.velcdn.com/images/alsgus92/post/92bded22-6a1b-49be-b8e5-d3630248071c/image.png](https://velog.velcdn.com/images/alsgus92/post/92bded22-6a1b-49be-b8e5-d3630248071c/image.png)
> 

### 🤔 동기화?

> 🍀 한 스레드가 진행 중인 작업을 다른 스레드가 간섭하지 못하도록 막는 것
자바의 `synchronized` 키워드를 이용하면 해당 메서드나 블록을 한 번에 한 스레드씩 수행하도록 보장한다.
> 
> 
> ***-멀티 스레드***와 ***동기화*** 스레드를 사용하게되면 필연적으로 만나게되는 문제
> 

### 🤔 멀티 스레드?

> **🍀 멀티 스레드는 하나의 프로세스 내부에서 여러 개의 스레드가 동시에 실행**되는 것이다. 
스레드끼리는 서로의 메모리 공간(thread stack)을 공유하고 접근할 수 있다. 
(+ **스레드는 프로세스 내부에서 실행되는 작은 작업 단위, 프로세스는 실행중인 프로그램)**
> 

### 참고) 멀티 프로세싱 vs 멀티 태스킹 vs 멀티 스레드

| 용어 | 멀티프로세싱(=multi processing) | 멀티태스킹(=multi tasking) | 멀티 스레딩(=multi threading) |
| --- | --- | --- | --- |
| 관점 | 시스템 관점 | 프로그램 외부에서의 관점 | 프로그램 내부에서의 관점 |
| 의미 | CPU 여러개에서 동시에 여러개의 프로세스 수행 | CPU 1개에서 동시에 여러 프로그램을 실행 | processor 1개가 동시에여러 스레드를 실행 |
| 구조 | 하나의 부모 프로세스가 여러 개의 자식 프로세스를 생성함으로서 다중 프로세스를 구성하는 구조 |  |  |
| 예시 | 웹 브라우저의 상단 탭(Tab) 이나 새 창
(각 브라우저 탭은 같은 브라우저 프로그램 실행이지만, 각기 다른 사이트를 실행함)

→ 하나의 탭이 먹통이어도, 다른 탭도 같이 먹통이 되진 않음 | pc카톡 켜놓고, youtube 음악들으면서, 온라인뱅킹 업무 | 웹 브라우저의 단일 탭 또는 창 내에서 브라우저 이벤트 루프, 네트워크 처리, I/O 및 기타 작업을 관리하고 처리 |

### 3️⃣ 회원 도메인 실행과 테스트

> 🍀 테스트 코드를 작성하는 이유는 기능을 개발할 때 잘 구현되었는지 작성하고, 리펙토링에 용의하기 때문이다. 이때 의도한 대로 기능이 정확하게 작동하는지 검증한다.
> 

테스트 코드 작성 방식으로 `given, when, then` 방식을 주로 사용한다.

- **given**테스트를 위해 준비를 하는 과정이다. 테스트에 사용하는 변수, 입력 값 등을 정의하거나 Mock 객체를 정의하는 구문도 포함된다.
- **when**실제로 액션을 하는 테스트를 실행한다.
- **then**테스트를 검증한다. 예상한 값이랑 실제로 실행해서 나온 값을 검증한다.[출처: Given-When-Then Pattern](https://brunch.co.kr/@springboot/292)

### 📍 회원 도메인 설계의 문제점

`MemberServiceImpl`를 보면 `MemberRepository` 인터페이스 뿐만 아니라 실제 구현체까지 의존하는 것을 확인할 수 있다. 즉 추상화와 구체화 모두 의존된다.  변경되었을 때 문제가 되며, DIP를 위반하게 된다.

```java
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();
```

### 4️⃣ 주문과 할인 도메인 설계

```
요구사항
- 상품 주문
- 회원 등급에 따른 할인 정책
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인 적용(변경 가능성 있음)
- 할인 정책은 변경가능성이 높다. 할인을 적용하지 않을 수도 있음(미확정)
```

- **주문 도메인 협력, 역할, 책임**

![https://velog.velcdn.com/images/wnajsldkf/post/f5ad8d25-2cfc-426a-9e69-b02ca7202d8c/image.png](https://velog.velcdn.com/images/wnajsldkf/post/f5ad8d25-2cfc-426a-9e69-b02ca7202d8c/image.png)

- 주문 생성시에 (id, 상품명, 상품가격)을 넘긴다.
- 할인을 적용하기 위해서는 회원 등급이 필요하므로, 주문 서비스는 회원저장소에서 회원을 조회한다.
- 주문 서비스는 회원 등급에 따른 할인여부를 할인 정책에 위임한다.
- 주문 서비스는 할인결과를 포함한 주문 결과를 클라이언트에 반환한다.

- **주문 도메인 클래스 다이어그램**
    
    ![https://velog.velcdn.com/images/wnajsldkf/post/86c17017-b245-4baa-bf1e-14360ca37d6e/image.png](https://velog.velcdn.com/images/wnajsldkf/post/86c17017-b245-4baa-bf1e-14360ca37d6e/image.png)
    
- **주문 도메인 객체 다이어 그램**
    
    ![https://velog.velcdn.com/images/wnajsldkf/post/56d87668-cc9f-4198-a3a2-f194f5079197/image.png](https://velog.velcdn.com/images/wnajsldkf/post/56d87668-cc9f-4198-a3a2-f194f5079197/image.png)
    
    - 역할들의 협력관계를 그대로 재사용 할 수 있다. (구현체를 얼마든지 다른 거로 갈아끼워도 됨)

### 📍 단위테스트 (작성 중 …) → 영한님이 중요하다고 강조

- 테스트 종류
    - **유닛 테스트:** 함수 하나하나와 같이 코드의 작은 부분을 테스트하는 것
    - **통합 테스트:** 서로 다른 시스템들의 상호작용이 잘 이루어 지는지 테스트하는 것
    - **기능 테스트:** 사용자와 어플리케이션의 상호작용이 원활하게 이루어지는지 테스트하는 것

### **유닛 테스트(Unit Test)**

---

유닛 테스트는 전체 코드 중 **작은 부분**을 테스트하는 것이다. (예를 들어, 함수 하나하나 개별로 테스트 코드를 작성하는 것) 만약 테스트에 네트워크나 데이터베이스 같은 외부 리소스가 포함된다면 그것은 유닛 테스트가 아니다.

또한, 유닛 테스트는 매우 간단하고 명확하여야 한다.

기본적으로 테스트를 위한 입력 값을 주어서 그에 대한 함수의 출력 값이 정확 한지 아닌지를 판단하는 것이 유닛 테스트라 할 수 있다.

- 인터넷 자료..

![스크린샷 2024-04-15 오후 8.10.57.png](%E1%84%89%E1%85%B3%E1%84%91%E1%85%B3%E1%84%85%E1%85%B5%E1%86%BC%20%E1%84%92%E1%85%A2%E1%86%A8%E1%84%89%E1%85%B5%E1%86%B7%20%E1%84%8B%E1%85%AF%E1%86%AB%E1%84%85%E1%85%B5%20-%20%E1%84%80%E1%85%B5%E1%84%87%E1%85%A9%E1%86%AB%E1%84%91%E1%85%A7%E1%86%AB%20c59234025fac4cb7ab2952fa3a7d639a/%25E1%2584%2589%25E1%2585%25B3%25E1%2584%258F%25E1%2585%25B3%25E1%2584%2585%25E1%2585%25B5%25E1%2586%25AB%25E1%2584%2589%25E1%2585%25A3%25E1%2586%25BA_2024-04-15_%25E1%2584%258B%25E1%2585%25A9%25E1%2584%2592%25E1%2585%25AE_8.10.57.png)

### **[⛅️ Assertions를 이용하여 두 객체가 같은지 확인](https://m42-orion.tistory.com/83#%E2%9B%85%EF%B8%8F%20Assertions%EB%A5%BC%20%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC%20%EB%91%90%20%EA%B0%9D%EC%B2%B4%EA%B0%80%20%EA%B0%99%EC%9D%80%EC%A7%80%20%ED%99%95%EC%9D%B8-1)**

`Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);`

위에서 코드에서 order.getDiscountPrice()의 반환값과 1000원이 같은지는 System.out.println을 통해 console 창에서 확인할 수도 있다.

하지만 테스트 코드의 수가 많아지면 console 창을 통해 계속 확인하기 힘들어 질 것이다.

이럴 때 assert라는 기능을 사용하면 유용하다.

**🍀 assert란?**

> JUnit에서 테스트에 넣을 수 있는 정적 메서드 호출
*어떤 조건이 참인지 검증*하며 *테스트 케이스 수행 결과를 판별하는 역할*을 한다.
> 

JUnit에서 크게 두 가지 Assert 스타일을 제공하는데, 첫 번째로 전통적인 스타일의 Assert는 junit의 원래 버전에 포함되어 있고, 두 번째로 hamcrest 버전의 assert는 junit 4.4부터 추가되었으며 전자에 비해 여러 장점이 있다.

**1️⃣ assertEquals() 이용**

참고 링크: [https://beomseok95.tistory.com/205](https://beomseok95.tistory.com/205)

```java
public void save(){
    Member member = new Member();
    member.setName("spring");

    repository.save(member);

    Member result = repository.findById(member.getId()).get();
    Assertions.assertEquals(member, result);
}
```

assertEquals(expected, actual) 의 형태로써, 객체 expected가 객체 actual와 같은지 확인한다.

만일 두 객체가 같다면 테스트 통과를 하게 된다.

**2️⃣ assertThat() 이용**

참고 링크 : [https://jongmin92.github.io/2020/03/31/Java/use-assertthat/](https://jongmin92.github.io/2020/03/31/Java/use-assertthat/)

```java
public void save(){
    Member member = new Member();
    member.setName("spring");

    repository.save(member);

    Member result = repository.findById(member.getId()).get();
    Assertions.assertThat(member).isEqualTo(result);
}
```

assertThat(actual).isEqualTo(expected) 의 형태로써, assertEquals와 같이 두 객체가 같은지 확인하고, 같다면 테스트 통과를 한다.

junit에서 두 객체가 같은지 확인하는 방법은 assertEquals와 assertThat이 있는데, 전자가 junit의 원래 버전에 포함되어 있는 assert이고, 후자가 hamcrest 버전의 assert이다.

최근에는 hamcrest 버전의 assert를 사용하는 것을 선호하는데, 가*독성, Failure 메세지, Type 안정성* 등 여러 부분에서 기존의 assert보다 나은 점들이 있기 때문이다.

가독성을 예로 들어보면, assertEquals(expected, actual)은 expected와 actual의 위치가 헷갈릴 때가 있다. (김영한님도 헷갈려하셨다!)

하지만 assertThat(actual).isEqualsTo(expected)는 영어 해석하듯이 넣으면 되기 때문에 assertEquals에 비해 expected와 actual이 들어갈 자리가 좀 더 명확히 보인다.

출처:[https://m42-orion.tistory.com/83#⛅️ Assertions를 이용하여 두 객체가 같은지 확인-1](https://m42-orion.tistory.com/83#%E2%9B%85%EF%B8%8F%20Assertions%EB%A5%BC%20%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC%20%EB%91%90%20%EA%B0%9D%EC%B2%B4%EA%B0%80%20%EA%B0%99%EC%9D%80%EC%A7%80%20%ED%99%95%EC%9D%B8-1)