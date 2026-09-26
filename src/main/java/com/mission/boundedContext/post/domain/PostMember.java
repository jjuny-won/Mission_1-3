package com.mission.boundedContext.post.domain;

import com.mission.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="POST_MEMBER")
@NoArgsConstructor
public class PostMember extends BaseEntity {

    @Id
    private int id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    @Column(unique = true)
    private String username;
    private String nickname;
    private int activityScore;

    public PostMember(int id, LocalDateTime createDate, LocalDateTime modifyDate,
                      String username, String nickname, int activityScore) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.username = username;
        this.nickname = nickname;
        this.activityScore = activityScore;
    }
}
