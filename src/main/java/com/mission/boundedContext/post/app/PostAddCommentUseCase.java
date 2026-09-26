package com.mission.boundedContext.post.app;

import com.mission.boundedContext.post.domain.Post;
import com.mission.boundedContext.post.domain.PostComment;
import com.mission.boundedContext.post.domain.PostMember;
import com.mission.boundedContext.post.out.PostRepository;
import com.mission.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAddCommentUseCase {
    private final PostRepository postRepository;

    public RsData<PostComment> addComment(int postId, PostMember author, String content) {
        Post post = postRepository.findById(postId).get();
        PostComment comment = post.addComment(author, content);
        return new RsData<>("201-1", "%d번 글에 댓글이 작성되었습니다.".formatted(postId), comment);
    }
}