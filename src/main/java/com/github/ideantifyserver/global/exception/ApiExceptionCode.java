package com.github.ideantifyserver.global.exception;

public interface ApiExceptionCode {

    String getCode();

    String getMessage();

    default ApiException toException() {

        return new ApiException(this);
    }
}
