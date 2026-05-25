package com.example.paymentservice.repository;

import com.example.paymentservice.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository
        extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity>
    findByIdempotencyKey(String key);
}
