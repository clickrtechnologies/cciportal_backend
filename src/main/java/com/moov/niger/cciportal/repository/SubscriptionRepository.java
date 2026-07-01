package com.moov.niger.cciportal.repository;

import com.moov.niger.cciportal.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query(value = """
    SELECT
        ts.msisdn,
        ts.sub_date,
        ts.req_date,
        ts.service_id,
        ts.product_id,
        ts.lang,
        ts.toneCode,
        tc.tone_name,
        ts.pack_name,
        ts.status,
        ts.user_status,
        ts.billing_date,
        ts.renew_date
    FROM tbl_subscription ts
            LEFT JOIN tbl_tone_catalogue tc
            ON ts.toneCode = tc.tone_code
    WHERE ts.msisdn = :msisdn
    """,
            nativeQuery = true)
    List<Object[]> getSubscriptionDetails(
            @Param("msisdn") Long msisdn);


    Optional<Subscription> findByMsisdn(Long msisdn);
}
