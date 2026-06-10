package com.moov.niger.cciportal.dto;

import lombok.Data;

@Data
public class SetRbtRequest {

    private Long msisdn;
    private String toneCode;
    private String toneName;
    private String packName;
}