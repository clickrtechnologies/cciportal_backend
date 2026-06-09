package com.moov.niger.cciportal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongResponse {

    private Long id;
    private String songCode;
    private String songName;
    private Integer duration;
    private String genre;
    private LocalDateTime uploadDate;
}
