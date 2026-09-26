package com.mission.boundedContext.post.app;

import com.mission.boundedContext.post.domain.PostMember;
import com.mission.boundedContext.post.out.PostMemberRepository;
import com.mission.shared.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostSyncMemberUseCase {

    private final PostMemberRepository postMemberRepository;

    public PostMember syncMember(MemberDto member){
        PostMember _member = new PostMember(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getUsername(),
                member.getNickname(),
                member.getActivityScore()
        );
        return postMemberRepository.save(_member);
    }
}
