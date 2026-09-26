package com.mission.shared.post.event;


import com.mission.shared.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostCreatedEvent {
    private final PostDto post;
}
