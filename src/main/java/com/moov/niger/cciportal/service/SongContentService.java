package com.moov.niger.cciportal.service;

import com.moov.niger.cciportal.dto.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongContentService {

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
}