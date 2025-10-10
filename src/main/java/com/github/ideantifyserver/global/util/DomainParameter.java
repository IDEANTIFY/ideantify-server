package com.github.ideantifyserver.global.util;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(schema = @Schema(type = "string", format = "uuid"))
public @interface DomainParameter {

    @AliasFor(annotation = Parameter.class, attribute = "description")
    String description() default "";
}
