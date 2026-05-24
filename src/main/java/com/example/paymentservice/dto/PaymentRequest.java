package com.example.paymentservice.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String userId;
    private BigDecimal amount;
    private String currency;
}