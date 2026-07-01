package com.moov.niger.cciportal.service;

import com.moov.niger.cciportal.dto.response.DeactivateResponse;
import com.moov.niger.cciportal.dto.request.SetRbtRequest;
import com.moov.niger.cciportal.dto.response.SetRbtResponse;
import com.moov.niger.cciportal.dto.response.SubscriptionDetailsResponse;
import com.moov.niger.cciportal.model.Subscription;
import com.moov.niger.cciportal.repository.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SubscriptionDetailsResponse getDetails(Long msisdn) {

        List<Object[]> result =
                subscriptionRepository.getSubscriptionDetails(msisdn);

        if (result.isEmpty()) {
            throw new RuntimeException(
                    "No active subscription found");
        }

        Object[] row = result.get(0);

        SubscriptionDetailsResponse response =
                new SubscriptionDetailsResponse();

        response.setMsisdn(((Number) row[0]).longValue());
        response.setSubDate((LocalDateTime) row[1]);
        response.setReqDate((LocalDateTime) row[2]);
        response.setServiceId(String.valueOf(row[3]));
        response.setProductId(String.valueOf(row[4]));
        response.setLang(String.valueOf(row[5]));
        response.setToneCode(String.valueOf(row[6]));
        response.setToneName(String.valueOf(row[7]));
        response.setPackName(String.valueOf(row[8]));
        response.setStatus(((Number) row[9]).byteValue());
        response.setUserStatus(String.valueOf(row[10]));
        response.setBillingDate((LocalDateTime) row[11]);
        response.setRenewDate((LocalDateTime) row[12]);
        return response;
    }

    @Transactional
    public SetRbtResponse activate(SetRbtRequest request) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime renewDate;

        switch (request.getPackName().toUpperCase()) {

            case "TSUBD":
                renewDate = now.plusDays(1);
                break;

            case "TSUBW":
                renewDate = now.plusDays(7);
                break;

            case "TSUBM":
                renewDate = now.plusDays(30);
                break;

            default:
                throw new RuntimeException("Invalid pack name");
        }

        StoredProcedureQuery procedure =
                entityManager.createStoredProcedureQuery("PROC_SUB_UNSUB");

        procedure.registerStoredProcedureParameter("IN_ANI", Long.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_PID", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_TONECODE", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_REQMODE", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_LANG", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_ACTION", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_PROMONAME", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IN_PROMOID", String.class, ParameterMode.IN);

        procedure.setParameter("IN_ANI", request.getMsisdn());
        procedure.setParameter("IN_PID", request.getPackName());
        procedure.setParameter("IN_TONECODE", request.getToneCode());
        procedure.setParameter("IN_REQMODE", "IVR");
        procedure.setParameter("IN_LANG", "fr");
        procedure.setParameter("IN_ACTION", "S");
        procedure.setParameter("IN_PROMONAME", "NA");
        procedure.setParameter("IN_PROMOID", "NA");

        procedure.execute();

        Subscription subscription =
                subscriptionRepository.findById(request.getMsisdn())
                        .orElseThrow(() ->
                                new RuntimeException("Subscription not found"));

        subscription.setToneCode(request.getToneCode());
        subscription.setPackName(request.getPackName());
        subscription.setBillingDate(now);
        subscription.setRenewDate(renewDate);
        subscription.setReqDate(now);


        subscriptionRepository.save(subscription);

        return new SetRbtResponse(
                "Current RBT set as ACTIVE",
                subscription.getMsisdn(),
                subscription.getPackName(),
                subscription.getBillingDate(),
                subscription.getRenewDate(),
                subscription.getReqDate(),
                subscription.getServiceId(),
                subscription.getProductId(),
                subscription.getToneCode(),
                subscription.getStatus(),
                subscription.getUserStatus()
        );
    }

    @Transactional
    public DeactivateResponse deactivate(Long msisdn) {

        Subscription subscription = subscriptionRepository.findById(msisdn)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Subscription not found"));

        DeactivateResponse response =
                new DeactivateResponse();

        response.setMessage("RBT deactivated successfully");
        response.setMsisdn(subscription.getMsisdn());
        response.setPackName(subscription.getPackName());
        response.setToneCode(subscription.getToneCode());
        response.setServiceId(subscription.getServiceId());
        response.setBillingDate(subscription.getBillingDate());
        response.setRenewDate(subscription.getRenewDate());
        response.setReqDate(subscription.getReqDate());
        response.setStatus(subscription.getStatus());
        response.setUserStatus(subscription.getUserStatus());

        StoredProcedureQuery procedure =
                entityManager.createStoredProcedureQuery(
                        "PROC_SUB_UNSUB");

        procedure.registerStoredProcedureParameter(
                "IN_ANI",
                Long.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_PID",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_TONECODE",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_REQMODE",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_LANG",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_ACTION",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_PROMONAME",
                String.class,
                ParameterMode.IN);

        procedure.registerStoredProcedureParameter(
                "IN_PROMOID",
                String.class,
                ParameterMode.IN);

        procedure.setParameter(
                "IN_ANI",
                subscription.getMsisdn());

        procedure.setParameter(
                "IN_PID",
                subscription.getProductId());

        procedure.setParameter(
                "IN_TONECODE",
                subscription.getToneCode());

        procedure.setParameter(
                "IN_REQMODE",
                "API");

        procedure.setParameter(
                "IN_LANG",
                subscription.getLang());

        procedure.setParameter(
                "IN_ACTION",
                "U");

        procedure.setParameter(
                "IN_PROMONAME",
                subscription.getPromoName());

        procedure.setParameter(
                "IN_PROMOID",
                subscription.getPromoId());

        procedure.execute();

        return response;
    }
}