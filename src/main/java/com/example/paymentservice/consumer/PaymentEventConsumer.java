package com.example.paymentservice.consumer;

import com.example.paymentservice.config.KafkaTopics;
import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.event.PaymentCreatedEvent;
import com.example.paymentservice.event.PaymentRetryEvent;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.producer.PaymentEventProducer;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final int MAX_RETRY = 3;

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentProducer;

    // =========================
    // MAIN PAYMENT EVENT CONSUMER
    // =========================
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATED_TOPIC,
            groupId = "payment-group"
    )
    public void consumePaymentCreated(PaymentCreatedEvent event) {

        System.out.println("Received payment event: " + event.getPaymentId());

        try {
            PaymentEntity payment = paymentRepository.findById(event.getPaymentId())
                    .orElseThrow();

            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);

            // simulate processing (replace with real logic later)
            Thread.sleep(1000);

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProcessedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            System.out.println("Payment completed: " + payment.getPaymentId());

        } catch (Exception ex) {

            System.out.println("Processing failed: " + ex.getMessage());

            PaymentRetryEvent retryEvent =
                    new PaymentRetryEvent(event, 1);

            paymentProducer.sendToRetry(retryEvent);
        }
    }

    // =========================
    // RETRY CONSUMER
    // =========================
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_RETRY_TOPIC,
            groupId = "payment-group"
    )
    public void consumeRetry(PaymentRetryEvent retryEvent) {

        int retryCount = retryEvent.getRetryCount();

        System.out.println("Retry attempt: " + retryCount);

        try {
            PaymentCreatedEvent originalEvent = retryEvent.getEvent();
            process(originalEvent);

        } catch (Exception ex) {

            int nextRetry = retryCount + 1;

            PaymentRetryEvent next = new PaymentRetryEvent(
                    retryEvent.getEvent(),
                    nextRetry
            );

            if (nextRetry <= MAX_RETRY) {
                paymentProducer.sendToRetry(next);
            } else {
                paymentProducer.sendToDLQ(next, ex.getMessage());
            }
        }
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void process(PaymentCreatedEvent event) {

        PaymentEntity entity = paymentRepository.findById(event.getPaymentId())
                .orElseThrow();

        entity.setStatus(PaymentStatus.SUCCESS);
        entity.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(entity);
    }
}