package com.github.ideantifyserver.global.exception;

import com.github.ideantifyserver.global.response.ApiResponse;

public interface ApiExceptionCode {

    String getCode();

    String getMessage();

    default ApiException toException() {

        return new ApiException(this);
    }

    default ApiResponse<?> toResponse() {

        return ApiResponse.error(getCode(), getMessage());
    }
}
