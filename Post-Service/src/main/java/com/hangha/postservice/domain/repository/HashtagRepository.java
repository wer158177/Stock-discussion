package com.hangha.postservice.domain.repository;


import com.hangha.postservice.domain.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
   Optional<Hashtag> findByName(String name);
}
