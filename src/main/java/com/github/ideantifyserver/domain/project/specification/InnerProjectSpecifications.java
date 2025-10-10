package com.github.ideantifyserver.domain.project.specification;

import com.github.ideantifyserver.domain.project.entity.InnerProject;
import com.github.ideantifyserver.domain.user.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class InnerProjectSpecifications {

    public static Specification<InnerProject> bookmarkedBy(User user) {
        return (root, cq, cb) -> {
            Join<Object, Object> bookmarks = root.join("bookmarks");
            cq.distinct(true);
            return cb.equal(bookmarks.get("user"), user);
        };
    }

    public static Specification<InnerProject> likedBy(User user) {
        return (root, cq, cb) -> {
            Join<Object, Object> likes = root.join("likes");
            cq.distinct(true);
            return cb.equal(likes.get("user"), user);
        };
    }

    public static Specification<InnerProject> memberOf(User user) {
        return (root, cq, cb) -> {
            Join<Object, Object> likes = root.join("members");
            cq.distinct(true);
            return cb.equal(likes.get("user"), user);
        };
    }

    
}
