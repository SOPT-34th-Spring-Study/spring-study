package server.sopt.practice.member;

public interface MemberService {
    void signIn(Member member);

    Member MemberFindById(Long id);
}