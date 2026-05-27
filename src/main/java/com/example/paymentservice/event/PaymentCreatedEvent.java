package com.example.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {

    private String paymentId;

    private Double amount;

    private String currency;

    private String payerId;

    private String payeeId;
}