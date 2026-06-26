package com.moov.niger.cciportal.service;

import com.moov.niger.cciportal.dto.SongResponse;
import com.moov.niger.cciportal.repository.SongContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongContentService {

    @Autowired
    private final SongContentRepository songContentRepository;

    private final JdbcTemplate jdbcTemplate;

    public List<SongResponse> getAllSongs() {

        String sql = """
        SELECT
            tone_code,
            tone_name,
            category,
            update_time
        FROM tbl_tone_catalogue
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            SongResponse dto = new SongResponse();

            dto.setToneCode(rs.getString("tone_code"));
            dto.setToneName(rs.getString("tone_name"));
            dto.setCategory(rs.getString("category"));

            dto.setUpdateTime(
                    rs.getTimestamp("update_time")
                            .toLocalDateTime()
            );

            return dto;
        });
    }


    public List<String> getCategoryList() {
        String sql = """
                SELECT DISTINCT category
                FROM tbl_tone_catalogue
                WHERE category IS NOT NULL
                  AND category <> ''
                ORDER BY category
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("category")
        );
    }

    public List<String> getArtistList() {
        String sql = """
                SELECT DISTINCT artist_name
                FROM tbl_tone_catalogue
                WHERE artist_name IS NOT NULL
                  AND artist_name <> ''
                ORDER BY artist_name
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("artist_name")
        );
    }


    public Map<String, Object> searchCatalog(
            int page,
            int size,
            String search,
            String category,
            String artist,
            String sort) {

        return songContentRepository.searchCatalog(page, size, search, category, artist, sort);
    }
}