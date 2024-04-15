package server.sopt.practice;

import server.sopt.practice.member.Grade;
import server.sopt.practice.member.Member;
import server.sopt.practice.member.MemberService;
import server.sopt.practice.member.MemberServiceImpl;
import server.sopt.practice.order.Order;
import server.sopt.practice.order.OrderService;
import server.sopt.practice.order.OrderServiceImpl;

public class OrderApp {
    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        OrderService orderService = new OrderServiceImpl();
        Long memberId = 1L;
        Member member = new Member(memberId, "kihoon", Grade.VIP);
        memberService.signIn(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);
        System.out.println("order : " + order);
        System.out.println("order Price " + order.caculatePrice());

    }
}
