package com.moov.niger.cciportal.repository;

import com.moov.niger.cciportal.model.SongContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongContentRepository extends JpaRepository<SongContent, Long> {

    Optional<SongContent> findByToneCode(String toneCode);
}
