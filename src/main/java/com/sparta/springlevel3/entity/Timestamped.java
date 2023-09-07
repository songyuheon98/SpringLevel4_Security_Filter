package com.sparta.springlevel3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 자동으로 시간 넣어주는 기능 수행하게 해줌
public abstract class Timestamped { // 다른 클래스에 상속하기 위한 클래스이므로 abstract(추상)

    @CreatedDate // entity 객체가 생성될 때 자동으로 시간 생성
    @Column(updatable = false) // 최초 시간만 가능, 변경시간 필요 x
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;


}