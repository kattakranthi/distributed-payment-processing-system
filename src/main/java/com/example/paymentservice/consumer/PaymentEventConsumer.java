package com.example.paymentservice.consumer;

import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.event.PaymentCreatedEvent;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentRepository
            repository;

    @KafkaListener(
            topics =
                    "payment-created-topic",
            groupId =
                    "payment-group")
    public void consumePaymentEvent(
            PaymentCreatedEvent event)
            throws InterruptedException {

        System.out.println(
                "Received payment event: "
                        + event);

        PaymentEntity payment =
                repository.findById(
                                event.getPaymentId())
                        .orElseThrow();

        payment.setStatus(
                PaymentStatus.PROCESSING);

        repository.save(payment);

        Thread.sleep(3000);

        payment.setStatus(
                PaymentStatus.FAILED);

        payment.setProcessedAt(
                LocalDateTime.now());

        payment.setUpdatedAt(
                LocalDateTime.now());

        repository.save(payment);

        System.out.println(
                "Payment completed: "
                        + payment.getPaymentId());
    }
}