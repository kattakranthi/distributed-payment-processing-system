package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentMetricsResponse;
import com.example.paymentservice.dto.PaymentStatusResponse;
import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentObservabilityService {

    private final PaymentRepository paymentRepository;

    public PaymentStatusResponse getPaymentStatus(
            String paymentId
    ) {

        PaymentEntity payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found: "
                                                + paymentId
                                )
                        );

        return PaymentStatusResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .payerId(payment.getPayerId())
                .payeeId(payment.getPayeeId())
                .status(payment.getStatus().name())
                .currency(payment.getCurrency())
                .idempotencyKey(
                        payment.getIdempotencyKey()
                )
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .processedAt(payment.getProcessedAt())
                .build();
    }

    public PaymentMetricsResponse getMetrics() {

        long successCount =
                paymentRepository.countByStatus(
                        PaymentStatus.SUCCESS
                );

        long failedCount =
                paymentRepository.countByStatus(
                        PaymentStatus.FAILED
                );

        long processingCount =
                paymentRepository.countByStatus(
                        PaymentStatus.PROCESSING
                );

        long totalPayments =
                paymentRepository.count();

        return PaymentMetricsResponse.builder()
                .successCount(successCount)
                .failedCount(failedCount)
                .processingCount(processingCount)
                .totalPayments(totalPayments)
                .build();
    }
}
