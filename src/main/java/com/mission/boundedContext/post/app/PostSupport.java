package com.mission.boundedContext.post.app;

import com.mission.boundedContext.post.domain.Post;
import com.mission.boundedContext.post.domain.PostMember;
import com.mission.boundedContext.post.out.PostMemberRepository;
import com.mission.boundedContext.post.out.PostRepository;
import com.mission.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostSupport {

    private final PostRepository postRepository;
    private final PostMemberRepository postMemberRepository;

    public long count(){
        return postRepository.count();
    }

    public Optional<PostMember> findByUsername(String username){
        return postMemberRepository.findByUsername(username);
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }
}
