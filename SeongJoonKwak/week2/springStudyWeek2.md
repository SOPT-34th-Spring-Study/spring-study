# 2주차 (section2)

## 비즈니스 요구사항과 설계

- 회원
    - 회원 가입 및 조회
    - 회원은 일반과 vip 등급
    - 데이터는 자체 데이터 혹은 외부 데이터 사용(미확정)
- 주문과 할인정책
    - 회원은 주문 가능
    - 회원 등급에 따른 할인정책
    - 할인정책은 vip는 무조건 1000원할인(추후변경가능)
    - 할인정책 변경 가능성 높음

→ 미확정 혹은 변경가능한 사항들이 있다 → 객체지향프로그래밍하면됨

- 인터페이스를 만들고, 구현체를 언제든 갈아끼울수 있도록 개발하자
- 스프링은 나중에 할것, 지금은 순수 자바로 구현

## 회원 도메인 설계

- 회원 도메인 협력 관계(도메인에 대한 큰 그림)
    
    <img width="522" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 24 29" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/21ea6e2b-8ef7-410b-a97d-cbff4689bbef">
    
    - 회원 저장소를 현재 자체 구축할 수도 있고, 외부 시스템과 연동할수도있음 (미확정)
    - 인터페이스 : 회원 저장소
    - 구현체 : 추후에 정해지는 저장소 방식(일단은 메모리 회원 저장소로 개발은 해두고, 추후에 정해지는대로 변경)

- 회원 클래스 다이어그램(실제 구현 레벨)
    
    <img width="526" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 27 51" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/95f1ad54-c551-4700-b837-fdc88b99d491">
    
    - 회원 서비스라는 인터페이스를 만들고, 구현체를 만듦(implement)
    - 위 회원 저장소가 memberRepository가 됨(인터페이스)
    - 그리고 메모리로 할지, 디비로 할지는 구현체로 결정(추후에 결정, 일단은 메모리방식)

- 회원 객체 다이어그램(실제 서버에 올라왔을 때, 객체간의 메모리 참조가 어떻게 되는지)
    
    <img width="655" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 31 09" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/51ef55ba-8b27-443a-bd88-034c23985a4f">
    
    - 회원 서비스 : MemberServiceImpl

## 회원 도메인 개발

- member 패키지
    - Grade(`enum`)
        
        ```java
        public enum Grade {
            BASIC,
            VIP
        }
        ```
        
    - Member(`class`)
        
        ```java
        package hello.core.member;
        
        public class Member {
            private Long id;
            private String name;
            private Grade grade;
        
            public Member(Long id, String name, Grade grade) {
                this.id = id;
                this.name = name;
                this.grade = grade;
            }
        
            public Long getId() {
                return id;
            }
        
            public void setId(Long id) {
                this.id = id;
            }
        
            public String getName() {
                return name;
            }
        
            public void setName(String name) {
                this.name = name;
            }
        
            public Grade getGrade() {
                return grade;
            }
        
            public void setGrade(Grade grade) {
                this.grade = grade;
            }
        }
        
        ```
        
    - MemberRepository(`interface`)
        
        ```java
        package hello.core.member;
        
        public interface MemberRepository {
            void save(Member member);
            Member findById(Long memberId);
        }
        
        ```
        
    - MemoryMemberRepository(`class`, MemberRepository 구현체)
        
        ```java
        package hello.core.member;
        
        import java.util.HashMap;
        import java.util.Map;
        
        public class MemoryMemberRepository implements MemberRepository{
        
            //저장소니까 map같은게 있어야됨
            //실무에서는 concurrent hashmap을 써야됨, 동시성이슈(동시에 접근하는 이슈)
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
        
    - MemberService(`interface`)
        
        ```java
        package hello.core.member;
        
        public interface MemberService {
            void join(Member member);
        
            Member fineMember(Long memberId);
        }
        
        ```
        
    - MemberServiceImpl
        
        ```java
        package hello.core.member;
        
        public class MemberServiceImpl implements MemberService {
        
            //가입을 하고 멤버를 찾으려면 memberRepository interface(memberRepository)가 필요함 -> 구현체도 필요함(MemoryMemberRespository)
            private final MemberRepository memberRepository = new MemoryMemberRepository();
        
            @Override
            public void join(Member member) {
                memberRepository.save(member);
        
            }
        
            @Override
            public Member fineMember(Long memberId) {
                return memberRepository.findById(memberId);
            }
        }
        
        ```
        
    
    - ConcurrentHashmap
        - bucket별로 동기화를 진행하므로 lock을 안건다.
        
        <img width="766" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_18 47 53" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/5ae1d9b6-6685-49f6-99f9-64c1ccdb026b">
        
    
    - HashMap
        - 한 스레드를 사용할 때, 나머지 스레드를 lock 거므로 안전하지만, 성능이 안좋음
        
        <img width="795" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_18 47 15" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/1e09db73-e97a-4c2c-acdc-149b079c600c">
        
    
    <aside>
    💬 구현체와 인터페이스를 서로 다른 패키지에 넣는 것이 좋지만, 간단한 예제이므로 같이 넣음
    
    </aside>
    
- memberService 테스트코드

```java
package hello.core.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService = new MemberServiceImpl();
    @Test
    void join() {

        //given(이런 이런 환경이 주어졌을 때)
        Member member = new Member(1L, "memberA", Grade.VIP);

        //when(이렇게 했을때)
        memberService.join(member);
        Member findMember = memberService.fineMember(1L);

        //then(이렇게 된다)
        Assertions.assertThat(member).isEqualTo(findMember);

    }
}
```

## 주문과 할인 도메인 설계

- 주문 도메인 역할
    
    <img width="405" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 36 54" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/dee9a8b5-32b9-4eef-8f72-8dc6e8767804">
    
    - 주문 생성 : 클라이언트는 주문 서비스에 주문 생성을 요청한다.
    - 회원 조회 : 할인을 위해서는 회원 등급 필요함, 그래서 주문 서비스는 회원 저장소에서 회원을 조회함
    - 할인 적용 : 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임함
    - 주문 결과 반환 : 주문 서비스는 할인 결과를 포함한 주문 결과를 반환한다.
    - 참고 : 실제로는 주문 데이터를 DB에 저장하지만, 간단한 예제이므로 단순히 주문 결과를 반환

- 주문 도메인 역할과 구현
    
    <img width="529" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 41 53" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/37e61793-3f9d-42e6-a0a0-6e47fbd628f2">
    
    - 역할(인터페이스)를 먼저 만들고, 구현체(impl)을 만듦
    - 할인 정책도 바뀔 수 있음(정액 or 정률) → 역할과 구현체로 분리
    
- 주문 도메인 클래스 다이어그램(객체 레벨로 구현)
    
    <img width="536" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 43 43" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/78d0342d-15e2-480c-95f6-b15e155d3e96">
    

- 주문 도메인 객체 다이어그램
    
    <img width="526" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-04-15_11 44 43" src="https://github.com/SOPT-34th-Spring-Study/spring-study-code/assets/70939232/0d9dd84f-7cf0-453a-a881-da6495ccb206">
    

## 주문과 할인정책 개발

- discount 패키지
    - DiscountPolicy(`interface`)
        
        ```java
        package hello.core.discount;
        
        import hello.core.member.Member;
        
        public interface DiscountPolicy {
        
            int discount(Member member, int price);
        }
        
        ```
        
    - FixDiscountPolicy(`class`, DiscountPolicy 구현체)
        
        ```java
        package hello.core.discount;
        
        import hello.core.member.Grade;
        import hello.core.member.Member;
        
        public class FixDiscountPolicy implements DiscountPolicy {
        
            private int discountFixAmount = 1000; //1000원 할
            @Override
            public int discount(Member member, int price) {
                if(member.getGrade() == Grade.VIP) {
                    return discountFixAmount;
                } else {
                    return 0;
                }
            }
        }
        
        ```
        

- order 패키지
    - Order(`class`)
        
        ```java
        package hello.core.order;
        
        public class Order {
            private Long memberId;
            private String memberName;
            private int itemPrice;
            private int discountPrice;
        
            public Order(Long memberId, String memberName, int itemPrice, int discountPrice) {
                this.memberId = memberId;
                this.memberName = memberName;
                this.itemPrice = itemPrice;
                this.discountPrice = discountPrice;
            }
        
            public int calculatePrice() {
                return itemPrice - discountPrice;
            }
        
            public Long getMemberId() {
                return memberId;
            }
        
            public void setMemberId(Long memberId) {
                this.memberId = memberId;
            }
        
            public String getMemberName() {
                return memberName;
            }
        
            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }
        
            public int getItemPrice() {
                return itemPrice;
            }
        
            public void setItemPrice(int itemPrice) {
                this.itemPrice = itemPrice;
            }
        
            public int getDiscountPrice() {
                return discountPrice;
            }
        
            public void setDiscountPrice(int discountPrice) {
                this.discountPrice = discountPrice;
            }
        
            @Override
            public String toString() {
                return "Order{" +
                        "memberId=" + memberId +
                        ", memberName='" + memberName + '\'' +
                        ", itemPrice=" + itemPrice +
                        ", discountPrice=" + discountPrice +
                        '}';
            }
        }
        
        ```
        
    - OrderService(`interface`)
        
        ```java
        package hello.core.order;
        
        public interface OrderService {
            Order createOrder(Long memberId, String itemName, int itemPrice);
        }
        
        ```
        
    - OrderServiceImpl(`class`, OrderService 구현체)
        
        ```java
        package hello.core.order;
        
        import hello.core.discount.DiscountPolicy;
        import hello.core.discount.FixDiscountPolicy;
        import hello.core.member.Member;
        import hello.core.member.MemberRepository;
        import hello.core.member.MemoryMemberRepository;
        
        public class OrderServiceImpl implements OrderService {
        
            private final MemberRepository memberRepository = new MemoryMemberRepository();
            private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
        
            @Override
            public Order createOrder(Long memberId, String itemName, int itemPrice) {
        
                Member member = memberRepository.findById(memberId);
                int discountPrice = discountPolicy.discount(member, itemPrice);
        
                return new Order(memberId, itemName, itemPrice, discountPrice);
            }
        }
        
        ```
        
        - discountPolicy가 있어서 단일책임원칙이 잘 지켜짐
            - 할인에 대한 변경이 들어와도 orderservice에서 변경이 없고, discountPolicy만 수정하면 됨
            

- orderService 테스트코드

```java
package hello.core.order;

import hello.core.member.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    MemberService memberService = new MemberServiceImpl();
    OrderService orderService = new OrderServiceImpl();

    @Test
    void createOrder() {
        Long memberId = 1L;
        Member memberA = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(memberA);

        Order order = orderService.createOrder(memberId, "itemA", 10000);
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);

    }
}

```