package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.exception.PaymentNotFoundException;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.IdempotencyService;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final IdempotencyService idempotencyService;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        // 1. Check idempotency
        Optional<PaymentEntity> existingPayment =
                idempotencyService.getExistingPayment(
                        request.getIdempotencyKey()
                );

        if (existingPayment.isPresent()) {
            return mapToResponse(existingPayment.get());
        }

        // 2. Create new payment
        PaymentEntity payment = PaymentEntity.builder()
                .paymentId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .idempotencyKey(request.getIdempotencyKey())
                .status(PaymentStatus.SUCCESS) // for Day 3 (simplified)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPayment(String paymentId) {

        PaymentEntity payment = repository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found: " + paymentId
                        )
                );

        return mapToResponse(payment);
    }

    // 3. Mapper method
    private PaymentResponse mapToResponse(PaymentEntity payment) {

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .message("Payment processed successfully")
                .build();
    }
}