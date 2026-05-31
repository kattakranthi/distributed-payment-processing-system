package com.example.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String payerId;

    @NotBlank
    private String payeeId;

    @NotBlank(message =
            "Idempotency key is required")
    private String idempotencyKey;
}

