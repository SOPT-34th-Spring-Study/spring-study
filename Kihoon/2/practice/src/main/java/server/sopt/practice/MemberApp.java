package server.sopt.practice;

import server.sopt.practice.member.Grade;
import server.sopt.practice.member.Member;
import server.sopt.practice.member.MemberService;
import server.sopt.practice.member.MemberServiceImpl;

public class MemberApp {
    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        Member member = new Member(1L, "MemberA", Grade.VIP);
        memberService.signIn(member);
        System.out.println("save");
        Member findMember = memberService.MemberFindById(1L);
        System.out.println("new Member  = " + member.getName());
        System.out.println("find Member  = " + findMember.getName());

    }
}
