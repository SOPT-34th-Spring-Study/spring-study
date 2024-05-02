package server.sopt.practice.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import server.sopt.practice.member.Grade;
import server.sopt.practice.member.Member;
import server.sopt.practice.member.MemberService;
import server.sopt.practice.member.MemberServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    MemberService memberService = new MemberServiceImpl();
    OrderService orderService = new OrderServiceImpl();

    @Test
    void createOrder(){
        Long memberid = 1L;
        Member member = new Member(memberid, "memberA", Grade.VIP);
        memberService.signIn(member);

        Order order = orderService.createOrder(memberid, "itemA", 10000);
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}