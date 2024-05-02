package server.sopt.practice.member;

public interface MemberRepository {
    void save(Member member);

    Member findById(Long id);
}
