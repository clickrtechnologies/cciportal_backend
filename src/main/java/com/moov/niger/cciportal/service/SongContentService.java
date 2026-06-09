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
                id,
                song_code,
                song_name,
                genre,
                duration,
                upload_date
            FROM song_content
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            SongResponse dto = new SongResponse();

            dto.setId(rs.getLong("id"));
            dto.setSongCode(rs.getString("song_code"));
            dto.setSongName(rs.getString("song_name"));
            dto.setDuration(rs.getInt("duration"));
            dto.setGenre(rs.getString("genre"));
            dto.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());

            return dto;
        });
    }
}