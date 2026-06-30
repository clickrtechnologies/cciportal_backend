package com.moov.niger.cciportal.service;

import com.moov.niger.cciportal.dto.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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

        StringBuilder sql = new StringBuilder("""
            SELECT
                tone_code,
                tone_name,
                category,
                artist_name,
                tone_url,
                update_time
            FROM tbl_tone_catalogue
            WHERE 1=1
            """);

        StringBuilder countSql = new StringBuilder("""
            SELECT COUNT(*)
            FROM tbl_tone_catalogue
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();
        List<Object> countParams = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {

            sql.append("""
                AND (
                    LOWER(tone_name) LIKE ?
                    OR LOWER(tone_code) LIKE ?
                    OR LOWER(artist_name) LIKE ?
                )
                """);

            countSql.append("""
                AND (
                    LOWER(tone_name) LIKE ?
                    OR LOWER(tone_code) LIKE ?
                    OR LOWER(artist_name) LIKE ?
                )
                """);

            String keyword = "%" + search.toLowerCase() + "%";

            params.add(keyword);
            params.add(keyword);
            params.add(keyword);

            countParams.add(keyword);
            countParams.add(keyword);
            countParams.add(keyword);
        }

        if (category != null && !category.trim().isEmpty()) {

            sql.append(" AND category = ? ");
            countSql.append(" AND category = ? ");

            params.add(category);
            countParams.add(category);
        }

        if (artist != null && !artist.trim().isEmpty()) {

            sql.append(" AND artist_name = ? ");
            countSql.append(" AND artist_name = ? ");

            params.add(artist);
            countParams.add(artist);
        }

        if ("newest".equalsIgnoreCase(sort)) {

            sql.append(" ORDER BY update_time DESC ");

        } else if ("oldest".equalsIgnoreCase(sort)) {

            sql.append(" ORDER BY update_time ASC ");

        } else {

            sql.append(" ORDER BY tone_code ASC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        params.add(size);
        params.add(page * size);

        List<SongResponse> songs = jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {

                    SongResponse dto = new SongResponse();

                    dto.setToneCode(rs.getString("tone_code"));
                    dto.setToneName(rs.getString("tone_name"));
                    dto.setCategory(rs.getString("category"));
                    dto.setArtistName(rs.getString("artist_name"));
                    dto.setToneUrl(rs.getString("tone_url"));
                    dto.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());

                    return dto;
                });

        Integer total = jdbcTemplate.queryForObject(
                countSql.toString(),
                Integer.class,
                countParams.toArray());

        Map<String, Object> response = new HashMap<>();

        response.put("content", songs);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return response;
    }
}