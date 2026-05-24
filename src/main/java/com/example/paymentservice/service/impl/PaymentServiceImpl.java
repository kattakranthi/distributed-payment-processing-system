package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.exception.PaymentNotFoundException;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = Payment.create(
                request.getAmount(),
                request.getCurrency(),
                request.getPayerId(),
                request.getPayeeId()
        );

        repository.save(payment);

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .message("Payment created successfully")
                .build();
    }

    @Override
    public PaymentResponse getPayment(String paymentId) {

        Payment payment = repository.findById(paymentId);

        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found: " + paymentId);
        }

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .message("Payment fetched successfully")
                .build();
    }
}