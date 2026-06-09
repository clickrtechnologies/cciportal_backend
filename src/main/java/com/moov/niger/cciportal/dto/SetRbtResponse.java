package com.moov.niger.cciportal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetRbtResponse {

    private String message;

    private Long msisdn;
    private String packName;

    private LocalDateTime billingDate;
    private LocalDateTime renewDate;
    private LocalDateTime reqDate;

    private String serviceId;
    private String productId;

    private String toneCode;

    private Byte status;
    private String userStatus;
}