package com.mission.boundedContext.member.app;

import com.mission.boundedContext.member.domain.Member;
import com.mission.boundedContext.member.out.MemberRepository;
import com.mission.global.eventPublisher.EventPublisher;
import com.mission.shared.member.event.MemberModifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberIncreaseActivityScoreUseCase {
    private final MemberRepository memberRepository;
    private final EventPublisher eventPublisher;

    public int increaseActivityScore(int memberId, int amount) {
        Member member = memberRepository.findById(memberId).get();
        int score = member.increaseActivityScore(amount);

        memberRepository.flush(); //수정 시각 확정 위함
        eventPublisher.publish(new MemberModifiedEvent(member.toDto()));

        return score;
    }
}