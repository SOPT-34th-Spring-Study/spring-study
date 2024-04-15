# **스프링 핵심 원리 이해1 - 예제 만들기**

## **비즈니스 요구사항과 설계**

**회원**

- 회원을 가입하고 조회할 수 있다.
- 회원은 일반과 VIP 두 가지 등급이 있다.
- 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)

**주문과 할인 정책**

- 회원은 상품을 주문할 수 있다.
- 회원 등급에 따라 할인 정책을 적용할 수 있다.
- 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
- 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)

## 회원 도메인 설계

- 회원 도메인 요구사항
    - 회원을 가입하고 조회할 수 있다.
    - 회원은 일반과 VIP 두 가지 등급이 있다.
    - 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)


### 회원 도메인

<img width="751" alt="01" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/dc4ddd77-6c0c-42a5-b5af-bc9cdc5a58b6">
<img width="765" alt="02" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/b6b3a9c1-22d5-4da3-9c86-1af6ebddae13">
<img width="751" alt="03" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/11a8fad7-0603-40de-a5fc-0dc184d58fd2">

## 회원 도메인 개발 및 실행과 테스트

https://github.com/SOPT-34th-Spring-Study/spring-study-code

### 회원 도메인 설계의 문제점

다른 저장소로 변경할 때 OCP 원칙을 잘 준수할까?
DIP를 잘 지키고 있을까?
**의존관계가 인터페이스 뿐만 아니라 구현까지 모두 의존하는 문제점이 있음**

**→ 주문까지 만들고나서 문제점과 해결 방안을 설명 (3주차에서)**

## 주문과 할인 도메인 설계

- 주문과 할인 정책
    - 회원은 상품을 주문할 수 있다.
    - 회원 등급에 따라 할인 정책을 적용할 수 있다.
    - 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
    - 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)


<img width="610" alt="04" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/099ac7f0-031c-4028-9d6d-15eb15dcc734">

※ 참고: 실제로는 주문 데이터를 DB에 저장하겠지만, 예제가 너무 복잡해 질 수 있어서 생략하고, 단순히 주문 결과를 반환

### 주문 도메인

<img width="627" alt="05" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/6a724502-f9db-4644-804b-1d981c8157b8">
<img width="636" alt="06" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/e6ff3568-edb4-4964-872e-8b2d1f22c9d0">

역할과 구현을 분리했기 때문에 구현 객체를 자유롭게 선택이 가능하다.
예를 들어, 회원 저장소를 메모리 회원 저장소에서 DB 회원 저장소로 바꾸고 싶다면, 역할 인터페이스는 이미 만들어 놓았기 때문에 DB 회원 저장소 구현 클래스로 바꾸어 넣기만 하면 된다.

<img width="647" alt="07" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/675c46c7-ab97-4eb0-86e4-21a96354075f">

역할들의 협력관계를 그대로 재사용 할 수 있다는 것이 핵심.

<img width="608" alt="08" src="https://github.com/SOPT-34th-Spring-Study/spring-study/assets/125895298/2cea738d-14ac-4b85-b2b1-f933bdd4d091">

회원을 메모리가 아닌 실제 DB에서 조회하고, 정률 할인 정책(주문 금액에 따라 % 할인)을 지원해도 주문 서비스를 변경하지 않아도 된다. (역할들의 협력관계 재사용)


## 주문과 할인 도메인 개발 및 실행과 테스트

https://github.com/SOPT-34th-Spring-Study/spring-study-code
