package com.moov.niger.cciportal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongResponse {

    private String toneCode;
    private String toneName;
    private String category;
    private LocalDateTime updateTime;
}
