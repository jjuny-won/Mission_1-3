package com.mission.boundedContext.post.out;

import com.mission.boundedContext.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findByOrderByIdDesc();
}
