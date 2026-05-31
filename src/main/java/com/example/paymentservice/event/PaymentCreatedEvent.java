package com.example.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {

    private String eventId;
    private String paymentId;
    private String payerId;
    private String payeeId;
    private String userId;
    private BigDecimal amount;
    private String currency;
}