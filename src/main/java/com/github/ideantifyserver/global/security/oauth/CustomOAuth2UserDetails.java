package com.github.ideantifyserver.global.security.oauth;

import com.github.ideantifyserver.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomOAuth2UserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2UserDetails(User user, Map<String, Object> attributes) {

        this.user = user;
        this.attributes = attributes;
    }

    public UUID getId() {

        return user.getId();
    }

    @Override
    public String getName() {

        return user.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {

        return "(EMPTY)";
    }

    @Override
    public String getUsername() {

        return user.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    public boolean hasKeywords() {

        return user.getKeywords() != null && !user.getKeywords().isEmpty();
    }
}
