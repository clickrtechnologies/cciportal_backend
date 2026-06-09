package com.moov.niger.cciportal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_subscription")
public class Subscription {

    @Id
    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "trans_id")
    private String transId;

    @Column(name = "toneCode")
    private String toneCode;

    @Column(name = "sub_date")
    private LocalDateTime subDate;

    @Column(name = "status")
    private Byte status;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "reqMode")
    private String reqMode;

    @Column(name = "req_date")
    private LocalDateTime reqDate;

    @Column(name = "renew_date")
    private LocalDateTime renewDate;

    @Column(name = "promoName")
    private String promoName;

    @Column(name = "promoId")
    private String promoId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "pack_name")
    private String packName;

    @Column(name = "no_of_retries")
    private Byte noOfRetries;

    @Column(name = "lang")
    private String lang;

    @Column(name = "fallback_string")
    private String fallbackString;

    @Column(name = "fallback_packs")
    private String fallbackPacks;

    @Column(name = "currentBalance")
    private String currentBalance;

    @Column(name = "charging_status")
    private Byte chargingStatus;

    @Column(name = "billing_date")
    private LocalDateTime  billingDate;

    @Column(name = "amount")
    private Float amount;
}

