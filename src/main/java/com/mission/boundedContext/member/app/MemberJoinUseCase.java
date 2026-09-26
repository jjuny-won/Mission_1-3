package com.mission.boundedContext.member.app;

import com.mission.boundedContext.member.domain.Member;
import com.mission.boundedContext.member.out.MemberRepository;
import com.mission.global.exception.DomainException;
import com.mission.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberJoinUseCase {

    private final MemberRepository memberRepository;

    public RsData<Member> join(String username, String password, String nickname) {
        memberRepository.findByUsername(username).ifPresent(m -> {
            throw new DomainException("409-1", "이미 존재하는 username 입니다.");
        });
        Member member = memberRepository.save((new Member(username, password, nickname)));
        return new RsData<>("201-1", "%d번 회원이 생성되었습니다.".formatted(member.getId()), member);
    }

}
