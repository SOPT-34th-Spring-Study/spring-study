package server.sopt.practice.discount;

import server.sopt.practice.member.Member;

public interface DiscountPolicy {
    // return 할인 대상 급액
    int discount(Member member, int price);

}
