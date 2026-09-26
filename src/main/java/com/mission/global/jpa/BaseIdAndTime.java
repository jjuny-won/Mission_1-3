package com.mission.global.jpa;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class) //JPA Auditing 기능 활성화
public abstract  class BaseIdAndTime  extends BaseEntity {
// 추상클래스로 구현한 이유 : 독립적인 Entity 생성 막기 위해 / 부모 클래스로만 사용하기 위해
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //AuditingListener가 작동할 때 어느 필드에 생성 시간과 수정 시간을 넣어야 할지 알려줘야하기 때문에 어노테이션 사용
    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

}
