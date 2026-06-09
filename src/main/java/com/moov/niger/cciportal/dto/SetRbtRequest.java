package com.moov.niger.cciportal.dto;

import lombok.Data;

@Data
public class SetRbtRequest {

    private Long msisdn;
    private String songCode;
    private String songName;
    private String packName;
}