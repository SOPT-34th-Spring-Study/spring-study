package server.sopt.practice.order;

import server.sopt.practice.discount.DiscountPolicy;
import server.sopt.practice.discount.FixDiscountPolicy;
import server.sopt.practice.member.Member;
import server.sopt.practice.member.MemberMemoryRepository;
import server.sopt.practice.member.MemberRepository;

public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemberMemoryRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
