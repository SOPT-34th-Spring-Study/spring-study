# 스프링 2주차

## 비즈니스 요구사항

- 회원
    - 가입, 조회
    - 등급 일반, vip 2가지
    - 회원 데이터 자체db or 외부 시스템(미확정)
- 주문과 할인 정책
    - 회원이 상품 주문
    - 회원 등급 따라 할인정책
    - vip는 1000원 고정 할인(추후 변경 가능)
    - 할인 정책 현재 미정, 오픈 직전까지 고민 미루고 싶음(미확정)

> 회원 데이터, 할인 정책 같은 미확정 부분은 인터페이스 만든 후 구현체를 언제든지 갈아끼울 수 있도록 설계하자.
> 

## 비즈니스 설계

- 회원 도메인 협력 관계
    
![1](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/1a5ebf95-b0ac-40f3-b457-afe0ba7428dc)
    
    - 회원 저장소가 미정이므로 인터페이스를 만든 후 구현.
    - 저장소 확정 전에 개발 용으로 `메모리 회원 저장소`를 만듦. 간단히 회원 객체 넣었다 뺄 수 있고, 서버 재부팅 시 저장된 데이터 없어짐.
    - 추후 저장소 확정되면 db 회원 저장소나 외부 시스템 연동 회원 저장소로 바꿈.
    - 기획자도 이해 가능.
- 회원 클래스 다이어그램
    
![2](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/92c9deb2-b716-4b4d-955a-de218446e222)
    
    - 도메인 협력 관계 바탕으로 개발자가  구체화.
    - 정적. 서버 실행 없이 클래스들만 분석 가능.  어떤 구현체를 넣을지는 서버 실행했을 때 동적으로 결정되므로 객체 다이어그램이 필요.
- 회원 객체 다이어그램
    
![3](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/8df9b9df-3240-4edb-ba52-9922c76a5e42)

    
    - 객체 간의 참조 관계 표현.
    - 회원 서비스 : MemberServiceImpl
    - 동적. 실제 new 한 인스턴스끼리의 참조 볼 수 있음.

## 회원 도메인 설계의 문제점

- 이 코드의 설계상 문제점
- 다른 저장소로 변경할 때 OCP 원칙을 잘 준수하는지
- DIP를 잘 지키는지
    - 아래 코드에서 `MemberServiceImpl`은 (추상화)인터페이스 `MemberService`를 의존하면서 실제 할당하는 부분은 (구체화)구현체 `MemoryMemberRepository`에 의존함. DIP 위반.
        
        ```jsx
        public class MemberServiceImpl implements MemberService{
        
            private final MemberRepository memberRepository = new MemoryMemberRepository();
        ```
        
    - 
- 의존 관계가 인터페이스 뿐만 아니라 구현까지 모두 의존함
    - 주문까지 만들고 나서 문제점과 해결 방안을 설명

## 주문과 할인 도메인 설계

- 주문 도메인 협력, 역할 , 책임
    
![Untitled](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/d039bd9c-3e34-4e09-b84b-9e066b181da1)

    
    1. 주문 생성: 클라이언트가 주문 서비스에 주문 생성 요청.
    2. 회원 조회: 주문서비스가 회원 저장소에서 회원 조회.
    3. 할인 적용: 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임
    4. 주문 결과 반환: 주문 서비스는 할인 결과를 포함한 주문 결과 반환.
    - 실제로는 주문 데이터를 DB에 저장하지만 예지 특성상 주문 결과 반환까지만 구현
- 주문 도메인 전체
    
![Untitled (1)](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/955377a0-269e-47ca-8521-c2ae59b348f2)
    
    - 역할 먼저 만든 후 구현. 즉 역할과 구현을 분리함. 이를 통해 자유롭게 구현 객체 조립 가능. 회원 저장소, 할인 정책 모두 유연하게 변경 가능.
- 주문 도메인 클래스 다이어그램
    
![Untitled (2)](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/740915c2-baa6-492b-b731-ffcb2721d8b2)

    
- 주문 도메인 객체 다이어그램1
    
![Untitled (3)](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/5324a669-7b96-4e1c-9a25-a2ad7e6a9f55)
    
    - 회원을 메모리에서 조회하고, 정액 할인 정책(고정 금액)을 지원해도 주문 서비스 변경 안해도 됨. 역할들의 협력 관계 그대로 재사용 가능.
    - DbMemberRepository가 메모리에서 db로 바뀌고 DiscountPolicy가 정액 할인에서 정률 할인 정책으로 바뀌어도 주문 서비스 구현체를 변경 안해도 된다는 뜻.
- 주문 도메인 객체 다이어그램2
    
![Untitled (4)](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/142b31d1-44f8-484f-a70a-54f3a6acbecd)
    
    - 회원을 메모리가 아닌 실제 db에서 조회하고,  정률 할인 정책(주문 금액 따라 % 할인)을 지원하는 케이스.
    - 주문 서비스 바꾸지 않고 협력 관계 그대로 재사용 가능.

## 단위 테스트의 중요성

- 스프링 같은 컨테이너의 도움 없이 순수하게 자바 코드만 테스트. JUnit.
- 실제 프로그램 전체 테스트에 수천 개의 단위 테스트가 실행되므로 단위 테스트 잘 만드는 게 중요
    
![Untitled (5)](https://github.com/SOPT-34th-Spring-Study/spring-study/assets/144998449/f27eaacd-6331-4c52-9288-5410a56db984)

    
- 예시 코드처럼 자동 생성된 test 폴더 안에 작성. `Assertions.assertThat().isEqaulTo()`로 테스트하고, Assertions는 `org.assertj.core.api` 로 선택.
- 기능 별로 테스트.
