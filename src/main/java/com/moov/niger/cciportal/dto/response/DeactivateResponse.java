package com.moov.niger.cciportal.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivateResponse {

    private String message;

    private Long msisdn;
    private String packName;
    private String toneCode;
    private String serviceId;

    private LocalDateTime billingDate;
    private LocalDateTime renewDate;
    private LocalDateTime reqDate;

    private Byte status;
    private String userStatus;
}