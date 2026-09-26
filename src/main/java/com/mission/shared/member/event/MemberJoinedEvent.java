package com.mission.shared.member.event;

import com.mission.shared.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberJoinedEvent {
    private final MemberDto member;
}
