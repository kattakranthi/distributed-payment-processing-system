package com.example.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paymentservice.entity.PaymentEntity;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByIdempotencyKey(String key);
}