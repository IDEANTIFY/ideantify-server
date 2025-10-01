package com.github.ideantifyserver.global.resolver;

import com.github.ideantifyserver.domain.auth.exception.AuthExceptions;
import com.github.ideantifyserver.domain.user.entity.AnonymousUser;
import com.github.ideantifyserver.domain.user.entity.UserContext;
import com.github.ideantifyserver.domain.user.repository.UserRepository;
import com.github.ideantifyserver.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && UserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            throw AuthExceptions.INVALID_REQUEST.toException();
        }

        // 인증된 사용자라면
        String authorization = request.getHeader("Authorization");

        if (Objects.nonNull(authorization) && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);
            if (jwtUtil.validateToken(token)) {

                UUID userId = jwtUtil.extractId(token);

                return userRepository.findById(userId)
                        .orElseThrow(AuthExceptions.USER_NOT_FOUND::toException);
            }
        }

        // 익명 사용자인 경우
        if (annotation.required()) {
            throw AuthExceptions.AUTHENTICATION_REQUIRED.toException();
        }

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("UserContext-Agent");

        String tempUserId = ipAddress + ":" + userAgent;

        // [ Redis에서 익명 사용자 조회 혹은 생성 ]

        // 1. Redis에서 캐시된 익명 사용자 조회
        Object cached = redisTemplate.opsForValue().get(tempUserId);
        if (cached instanceof AnonymousUser) {
            return (AnonymousUser) cached;
        }

        // 2. 캐시되지 않았다면 새 익명 사용자 생성 및 캐싱
        AnonymousUser anonymousUser = AnonymousUser.builder().userId(tempUserId).build();

        redisTemplate.opsForValue().set(tempUserId, anonymousUser);

        return anonymousUser;
    }
}