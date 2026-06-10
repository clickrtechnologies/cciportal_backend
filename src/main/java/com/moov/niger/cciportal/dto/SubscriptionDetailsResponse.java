package com.moov.niger.cciportal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDetailsResponse {

    private Long msisdn;
    private LocalDateTime subDate;
    private LocalDateTime reqDate;
    private String serviceId;
    private String productId;
    private String lang;
    private String toneCode;
    private String toneName;
    private String packName;
    private Byte status;
    private String userStatus;
    private LocalDateTime billingDate;
    private LocalDateTime renewDate;
}