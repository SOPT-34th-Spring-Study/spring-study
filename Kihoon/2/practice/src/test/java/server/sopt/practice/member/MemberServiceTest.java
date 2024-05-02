package server.sopt.practice.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {
    MemberService memberService = new MemberServiceImpl();
    @Test
    void sighin(){
        //given
        Member member = new Member(1L, "kihoon", Grade.VIP);
        //when
        memberService.signIn(member);
        Member member1 = memberService.MemberFindById(1L);
        //then
        Assertions.assertThat(member).isEqualTo(member1);
    }

}