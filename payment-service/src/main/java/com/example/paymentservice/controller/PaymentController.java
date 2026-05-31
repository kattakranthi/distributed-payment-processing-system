package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.dto.PaymentStatusUpdateRequest;
import com.example.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable String paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse>
    updatePaymentStatus(

            @PathVariable String paymentId,

            @RequestBody
            PaymentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                paymentService
                        .updatePaymentStatus(
                                paymentId,
                                request));
    }
}