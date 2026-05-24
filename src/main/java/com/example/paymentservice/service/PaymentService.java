package com.example.paymentservice.service;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final Supplier<String> idGenerator;

    public PaymentService(PaymentRepository repository,
                          Supplier<String> idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    public Payment createPayment(Payment payment) {
        payment.setPaymentId(idGenerator.get());
        payment.setStatus(PaymentStatus.INITIATED);

        return repository.save(payment);
    }

    public Payment getPayment(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}