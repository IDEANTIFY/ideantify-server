package com.github.ideantifyserver.domain.project.specification;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class InnerProjectSpecifications {
    public static Specification<InnerProject> bookmarkedBy(UUID userId) {
        return (root, cq, cb) -> {
            Join<Object, Object> bookmarks = root.join("bookmarks");
            cq.distinct(true);
            return cb.equal(bookmarks.get("user").get("id"), userId);
        };
    }

    public static Specification<InnerProject> likedBy(UUID userId) {
        return (root, cq, cb) -> {
            Join<Object, Object> likes = root.join("likes");
            cq.distinct(true);
            return cb.equal(likes.get("user").get("id"), userId);
        };
    }

    public static Specification<InnerProject> memberOf(UUID userId) {
        return (root, cq, cb) -> {
            Join<Object, Object> likes = root.join("members");
            cq.distinct(true);
            return cb.equal(likes.get("user").get("id"), userId);
        };
    }

    
}
