package com.example.paymentservice.repository;

import com.example.paymentservice.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentRepository {

    private final Map<String, Payment> store = new ConcurrentHashMap<>();

    public Payment save(Payment payment) {
        store.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(store.get(paymentId));
    }
}