package com.mission.boundedContext.post.app;

import com.mission.boundedContext.post.domain.Post;
import com.mission.boundedContext.post.domain.PostComment;
import com.mission.boundedContext.post.domain.PostMember;
import com.mission.global.rsData.RsData;
import com.mission.shared.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostSupport postSupport;
    private final PostWriteUseCase postWriteUseCase;
    private final PostSyncMemberUseCase  postSyncMemberUseCase;
    private final PostAddCommentUseCase postAddCommentUseCase;

    public long count(){
        return postSupport.count();}

    @Transactional(readOnly = true)
    public Optional<PostMember> findByUsername(String username){
        return postSupport.findByUsername(username);
    }
    @Transactional
    public RsData<Post> write(PostMember author, String title, String content){
        return postWriteUseCase.write(author, title, content);
    }

    public Optional<Post> findById(int id) { return postSupport.findById(id);
    }

    @Transactional
    public PostMember syncMember(MemberDto member){
        return postSyncMemberUseCase.syncMember(member);
    }

    @Transactional
    public RsData<PostComment> addComment(int postId, PostMember author, String content) {
        return postAddCommentUseCase.addComment(postId, author, content);
    }
}
