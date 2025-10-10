package com.github.ideantifyserver.domain.keyword.repository;

import com.github.ideantifyserver.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

    List<Keyword> findByNameIn(Collection<String> names);
}
