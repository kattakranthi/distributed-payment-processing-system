package com.example.paymentservice.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Payment {

    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String payerId;
    private String payeeId;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public static Payment create(BigDecimal amount, String currency, String payerId, String payeeId) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setPayerId(payerId);
        payment.setPayeeId(payeeId);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }
}