package com.example.events;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDLQEvent {

    private PaymentCreatedEvent event;
    private int retryCount;
    private String errorType;
    private String errorMessage;
    private LocalDateTime failedAt;

    public PaymentDLQEvent(PaymentRetryEvent retryEvent) {
        this.event = retryEvent.getEvent();
        this.retryCount = retryEvent.getRetryCount();
        this.errorType = retryEvent.getErrorType();
        this.errorMessage = retryEvent.getLastError();
        this.failedAt = LocalDateTime.now();
    }
}