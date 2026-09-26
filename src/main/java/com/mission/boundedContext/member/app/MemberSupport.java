package com.mission.boundedContext.member.app;

import com.mission.boundedContext.member.out.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSupport {

    private final MemberRepository memberRepository;

    public long count(){
        return memberRepository.count();
    }

}
