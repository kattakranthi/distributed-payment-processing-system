package com.example.paymentservice.service;

import com.example.paymentservice.entity.PaymentEntity;

import java.util.Optional;

public interface IdempotencyService {

    Optional<PaymentEntity> getExistingPayment(String key);
}