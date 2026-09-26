package com.mission.boundedContext.member.app;

import com.mission.boundedContext.member.domain.Member;
import com.mission.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberJoinUseCase memberJoinUseCase;
    private final MemberSupport memberSupport;
    private final MemberGetRandomSecureTipUseCase memberGetRandomSecureTipUseCase;

    @Transactional
    public RsData<Member> join(String username, String password, String nickname) {
        return memberJoinUseCase.join(username, password, nickname);
    }

    @Transactional(readOnly = true)
    public long count(){
        return memberSupport.count();
    }

    public String getRandomSecureTip() {
        return (memberGetRandomSecureTipUseCase.getRandomSecureTip());
    }
}
