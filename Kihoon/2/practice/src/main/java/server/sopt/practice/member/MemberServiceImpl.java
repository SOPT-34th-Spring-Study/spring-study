package server.sopt.practice.member;

public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository = new MemberMemoryRepository();

    @Override
    public void signIn(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member MemberFindById(Long id) {
        return memberRepository.findById(id);
    }
}