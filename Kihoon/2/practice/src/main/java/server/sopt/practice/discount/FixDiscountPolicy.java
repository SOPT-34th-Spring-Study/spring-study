package server.sopt.practice.discount;

import server.sopt.practice.member.Grade;
import server.sopt.practice.member.Member;

public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade()== Grade.VIP){
            return discountFixAmount;
        }
        return 0;
    }
}
