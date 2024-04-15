package server.sopt.practice.member;

import java.util.HashMap;
import java.util.Map;

public class MemberMemoryRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long id) {
        return store.get(id);
    }
}