package com.mission.boundedContext.post.app;

import com.mission.boundedContext.post.domain.Post;
import com.mission.boundedContext.post.domain.PostMember;
import com.mission.boundedContext.post.out.PostRepository;
import com.mission.global.eventPublisher.EventPublisher;
import com.mission.global.rsData.RsData;
import com.mission.shared.member.out.MemberApiClient;
import com.mission.shared.post.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostWriteUseCase {

    private final PostRepository postRepository;
    private final MemberApiClient memberApiClient;
    private final EventPublisher eventPublisher;

    public RsData<Post> write(PostMember author, String title, String content){
        Post post = postRepository.save(new Post(author, title, content ));

        eventPublisher.publish(new PostCreatedEvent(post.toDto()));
        String randomSecureTip = memberApiClient.getRandomSecureTip();

        return new RsData<>("201-1",
                "%d번 글이 생성되었습니다. 보안 팁 : %s"
                        .formatted(post.getId(), randomSecureTip),
                post
        );
    }
}
