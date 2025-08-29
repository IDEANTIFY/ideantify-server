package com.github.ideantifyserver.global.infra.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public abstract class BaseSchema {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    @NotNull
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    UUID id;

    @Column(nullable = false, updatable = false)
    @NotNull
    @CreatedDate
    LocalDateTime createdAt;

    @Column(nullable = false)
    @NotNull
    @LastModifiedDate
    LocalDateTime updatedAt;
}
