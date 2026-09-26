package com.mission.boundedContext.member.app;

import com.mission.boundedContext.member.domain.Member;
import com.mission.boundedContext.member.out.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberIncreaseActivityScoreUseCase {
    private final MemberRepository memberRepository;

    public int increaseActivityScore(int memberId, int amount) {
        Member member = memberRepository.findById(memberId).get();
        return member.increaseActivityScore(amount);
    }
}