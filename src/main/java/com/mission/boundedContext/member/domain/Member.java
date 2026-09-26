package com.mission.boundedContext.member.domain;

import com.mission.global.jpa.BaseIdAndTime;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseIdAndTime {
    private String username;
    private String password;
    private String nickname;
    private int activityScore;

    // 꼭 필요한 값 (Id, Date 제외) 만 받는 생성자
    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
