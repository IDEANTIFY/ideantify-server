package com.github.ideantifyserver.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnonymousUser implements UserContext {

    @Id
    @NotNull
    @Column(columnDefinition = "TEXT")
    String userId;
}
