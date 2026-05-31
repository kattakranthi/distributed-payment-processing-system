package com.example.paymentservice.dto;

import com.example.paymentservice.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {

    private String paymentId;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String message;

    private LocalDateTime createdAt;
}