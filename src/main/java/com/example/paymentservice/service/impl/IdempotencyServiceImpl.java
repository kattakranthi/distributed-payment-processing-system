package com.example.paymentservice.service.impl;

import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final PaymentRepository paymentRepository;

    @Override
    public Optional<PaymentEntity> getExistingPayment(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey);
    }
}
