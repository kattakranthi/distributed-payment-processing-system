package com.example.paymentservice.controller;


import com.example.paymentservice.dto.PaymentMetricsResponse;
import com.example.paymentservice.dto.PaymentStatusResponse;
import com.example.paymentservice.service.PaymentObservabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentObservabilityController {

    private final PaymentObservabilityService
            observabilityService;

    @GetMapping("/{paymentId}")
    public PaymentStatusResponse getPaymentStatus(
            @PathVariable String paymentId
    ) {

        return observabilityService
                .getPaymentStatus(paymentId);
    }

    @GetMapping("/metrics")
    public PaymentMetricsResponse getMetrics() {

        return observabilityService
                .getMetrics();
    }
}