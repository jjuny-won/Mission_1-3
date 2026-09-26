package com.mission.boundedContext.member.in;

import com.mission.boundedContext.member.app.MemberFacade;
import com.mission.boundedContext.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
public class MemberDataInit {

    private final MemberDataInit self;
    private final MemberFacade memberFacade;

    public MemberDataInit(
            @Lazy MemberDataInit self, // 트랜잭션이 적용되도록 프록시 객체를 통한 호출
            MemberFacade memberFacade
    ) {
        this.self = self;
        this.memberFacade = memberFacade;
    }

    //스프링 부트 애플리케이션 실행 직후 딱 한 번 자동으로 실행되는 인터페이스
    @Bean
    @Order(1)
    public ApplicationRunner baseInitDataRunner() {
        return args -> {
            self.makeBaseMembers();
        };
    }

    @Transactional
    public void makeBaseMembers(){
        if(memberFacade.count()>0) return;
        Member systemMember = memberFacade.join("system", "1234", "시스템").getData();
        Member holdingMember = memberFacade.join("holding", "1234", "홀딩").getData();
        Member adminMember = memberFacade.join("admin", "1234", "관리자").getData();
        Member user1Member = memberFacade.join("user1", "1234", "유저1").getData();
        Member user2Member = memberFacade.join("user2", "1234", "유저2").getData();
        Member user3Member = memberFacade.join("user3", "1234", "유저3").getData();

    }


}
