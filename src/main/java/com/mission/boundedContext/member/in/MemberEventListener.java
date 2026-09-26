package com.mission.boundedContext.member.in;

import com.mission.boundedContext.member.app.MemberFacade;
import com.mission.boundedContext.member.domain.Member;
import com.mission.shared.post.event.PostCommentCreatedEvent;
import com.mission.shared.post.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class MemberEventListener {
    private final MemberFacade memberFacade;

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle (PostCreatedEvent event) {
        Member member = memberFacade.findById(event.getPost().getAuthorId()).get();
        memberFacade.increaseActivityScore(member.getId(),3);
    }

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle (PostCommentCreatedEvent event) {
        Member member = memberFacade.findById(event.getPostComment().getAuthorId()).get();
        memberFacade.increaseActivityScore(member.getId(), 1);
    }
}
