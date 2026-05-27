package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.dto.PaymentStatusUpdateRequest;
import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.exception.InvalidPaymentStatusException;
import com.example.paymentservice.exception.PaymentNotFoundException;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.IdempotencyService;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.paymentservice.event.PaymentCreatedEvent;
import com.example.paymentservice.producer.PaymentEventProducer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentEventProducer
            paymentEventProducer;


    private final IdempotencyService
            idempotencyService;

    @Override
    public PaymentResponse createPayment(
            PaymentRequest request) {

        Optional<PaymentEntity>
                existingPayment =
                idempotencyService
                        .getExistingPayment(
                                request.getIdempotencyKey());

        if (existingPayment.isPresent()) {

            return mapToResponse(
                    existingPayment.get());
        }

        PaymentEntity payment =
                PaymentEntity.builder()
                        .paymentId(
                                UUID.randomUUID()
                                        .toString())
                        .amount(
                                request.getAmount())
                        .currency(
                                request.getCurrency())
                        .payerId(
                                request.getPayerId())
                        .payeeId(
                                request.getPayeeId())
                        .idempotencyKey(
                                request.getIdempotencyKey())
                        .status(
                                PaymentStatus.PENDING)
                        .createdAt(
                                LocalDateTime.now())
                        .updatedAt(
                                LocalDateTime.now())
                        .build();

        repository.save(payment);

        PaymentCreatedEvent event =
                PaymentCreatedEvent
                        .builder()
                        .paymentId(
                                payment.getPaymentId())
                        .amount(
                                payment.getAmount())
                        .currency(
                                payment.getCurrency())
                        .payerId(
                                payment.getPayerId())
                        .payeeId(
                                payment.getPayeeId())
                        .build();

        paymentEventProducer
                .publishPaymentEvent(
                        event);

        return mapToResponse(
                payment);
    }

    @Override
    public PaymentResponse getPayment(
            String paymentId) {

        PaymentEntity payment =
                repository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found: "
                                                + paymentId));

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse updatePaymentStatus(
            String paymentId,
            PaymentStatusUpdateRequest request) {

        PaymentEntity payment =
                repository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found: "
                                                + paymentId));

        PaymentStatus status;

        try {
            status = PaymentStatus.valueOf(
                    request.getStatus()
                            .toUpperCase());

        } catch (Exception e) {

            throw new
                    InvalidPaymentStatusException(
                    "Invalid status: "
                            + request.getStatus());
        }

        payment.setStatus(status);
        payment.setUpdatedAt(
                LocalDateTime.now());

        if (status == PaymentStatus.SUCCESS
                || status == PaymentStatus.FAILED) {

            payment.setProcessedAt(
                    LocalDateTime.now());
        }

        repository.save(payment);

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(
            PaymentEntity payment) {

        return PaymentResponse.builder()
                .paymentId(
                        payment.getPaymentId())
                .amount(
                        payment.getAmount())
                .currency(
                        payment.getCurrency())
                .status(
                        payment.getStatus().name())
                .message(
                        "Payment processed successfully")
                .createdAt(
                        payment.getCreatedAt())
                .build();
    }
}