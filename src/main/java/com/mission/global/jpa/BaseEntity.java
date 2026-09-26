package com.mission.global.jpa;


import com.mission.global.global.GlobalConfig;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //직접 테이블 생성은 안되지만, 자식 엔티티에게 매핑 정보 상속해주는 클래스
public abstract class BaseEntity {

    public abstract int getId();

    public abstract LocalDateTime getCreateDate();

    public abstract LocalDateTime getModifyDate();

    protected void publishEvent(Object event) {
        GlobalConfig.getEventPublisher().publish(event);
    }
}
