package com.mission.boundedContext.post.in;


import com.mission.boundedContext.post.app.PostFacade;
import com.mission.boundedContext.post.domain.Post;
import com.mission.boundedContext.post.domain.PostMember;
import com.mission.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
public class PostDataInit {

    private final PostDataInit self;
    private final PostFacade postFacade;

    public PostDataInit(@Lazy PostDataInit self, PostFacade postFacade) {
        this.self = self;
        this.postFacade = postFacade;
    }

    @Bean
    @Order(2) //Beam 의 우선순위를 정하기 위함 - Member 후 Post 를 적용하기 위해
    public ApplicationRunner postDataInitApplicationRunner() {
        return args -> {
            self.makeBasePosts();
            self.makeBasePostComments();
        };
    }

    @Transactional
    public void makeBasePosts(){
        if (postFacade.count()>0) return;

        PostMember user1Member = postFacade.findByUsername("user1").get();
        PostMember user2Member = postFacade.findByUsername("user2").get();
        PostMember user3Member = postFacade.findByUsername("user3").get();

        RsData<Post> post1RsData = postFacade.write(user1Member, "제목1", "내용1");
        log.debug(post1RsData.getMsg());

        RsData<Post> post2RsData = postFacade.write(user1Member, "제목2", "내용2");
        log.debug(post2RsData.getMsg());

        RsData<Post> post3RsData = postFacade.write(user1Member, "제목3", "내용3");
        log.debug(post3RsData.getMsg());

        RsData<Post> post4RsData = postFacade.write(user2Member, "제목4", "내용4");
        log.debug(post4RsData.getMsg());

        RsData<Post> post5RsData = postFacade.write(user2Member, "제목5", "내용5");
        log.debug(post5RsData.getMsg());

        RsData<Post> post6RsData = postFacade.write(user3Member, "제목6", "내용6");
        log.debug(post6RsData.getMsg());
    }

    @Transactional
    public void makeBasePostComments(){

        Post post1 = postFacade.findById(1).get();
        Post post2 = postFacade.findById(2).get();
        Post post3 = postFacade.findById(3).get();
        Post post4 = postFacade.findById(4).get();

        PostMember user1Member = postFacade.findByUsername("user1").get();
        PostMember user2Member = postFacade.findByUsername("user2").get();
        PostMember user3Member = postFacade.findByUsername("user3").get();

        if (post1.hasComments()) return;

        postFacade.addComment(post1.getId(), user1Member, "댓글1");
        postFacade.addComment(post1.getId(), user2Member, "댓글2");
        postFacade.addComment(post1.getId(), user3Member, "댓글3");

        postFacade.addComment(post2.getId(), user2Member, "댓글4");
        postFacade.addComment(post2.getId(),user2Member, "댓글5");

        postFacade.addComment(post3.getId(),user3Member, "댓글6");
        postFacade.addComment(post3.getId(),user3Member, "댓글7");

        postFacade.addComment(post4.getId(),user1Member, "댓글8");

    }
}
