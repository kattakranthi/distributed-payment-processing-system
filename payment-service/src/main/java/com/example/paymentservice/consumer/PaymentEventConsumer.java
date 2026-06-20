package com.example.paymentservice.consumer;

import com.example.events.PaymentCompletedEvent;
import com.example.events.PaymentCreatedEvent;
import com.example.events.PaymentRetryEvent;
import com.example.paymentservice.config.KafkaTopics;
import com.example.paymentservice.entity.PaymentEntity;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.producer.PaymentEventProducer;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final int MAX_RETRY = 3;

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentProducer;



    // =========================
    // MAIN PAYMENT CONSUMER
    // =========================
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATED_TOPIC,
            groupId = "payment-group"
    )
    public void consumePaymentCreated(PaymentCreatedEvent event) {

        log.info("Received payment event paymentId={}", event.getPaymentId());

        try {

            PaymentEntity payment = paymentRepository.findById(event.getPaymentId().toString())
                    .orElseThrow(() ->
                            new RuntimeException("Payment not found: " + event.getPaymentId())
                    );
            MDC.put("correlationId", event.getCorrelationId());

            log.info("Processing payment event {}", event.getPaymentId());
            log.info("TEST LOG");

            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);

            // simulate processing
            Thread.sleep(1000);

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProcessedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            log.info("Payment completed paymentId={}", payment.getPaymentId());

            PaymentCompletedEvent completedEvent =
                    new PaymentCompletedEvent(
                            payment.getPaymentId().toString(),
                            payment.getAmount(),
                            payment.getStatus().name(),
                            payment.getPayerId(),
                            payment.getPayeeId()
                    );

            paymentProducer.publishPaymentCompleted(completedEvent);

        } catch (Exception ex) {

            log.error("Processing failed paymentId={} error={}",
                    event.getPaymentId(),
                    ex.getMessage());

            PaymentRetryEvent retryEvent =
                    new PaymentRetryEvent(
                            event,
                            1,
                            ex.getClass().getSimpleName(),
                            ex.getMessage()
                    );

            paymentProducer.sendToRetry(retryEvent);
        } finally {
            MDC.clear();
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
        PaymentCreatedEvent event = retryEvent.getEvent();

        log.info("Retry attempt={} paymentId={}",
                retryCount,
                event.getPaymentId());

        try {

            // 🔐 FIXED: correct Optional handling
            if (paymentRepository.findByIdempotencyKey(event.getIdempotencyKey()).isPresent()) {
                log.info("Duplicate payment ignored idempotencyKey={}",
                        event.getIdempotencyKey());
                return;
            }

            process(event);

            log.info("Retry success paymentId={}", event.getPaymentId());

        } catch (Exception ex) {

            int nextRetry = retryCount + 1;

            log.error("Retry failed paymentId={} attempt={} error={}",
                    event.getPaymentId(),
                    retryCount,
                    ex.getMessage());

            PaymentRetryEvent nextEvent = new PaymentRetryEvent(
                    event,
                    nextRetry,
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );

            if (nextRetry <= MAX_RETRY) {
                paymentProducer.sendToRetry(nextEvent);
            } else {
                paymentProducer.sendToDLQ(
                        buildDLQEvent(nextEvent)
                );
            }
        }
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void process(PaymentCreatedEvent event) {

        PaymentEntity entity = paymentRepository.findById(event.getPaymentId().toString())
                .orElseThrow(() ->
                        new RuntimeException("Payment not found: " + event.getPaymentId())
                );

        entity.setStatus(PaymentStatus.SUCCESS);
        entity.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(entity);
    }

    // =========================
    // DLQ BUILDER
    // =========================
    private com.example.events.PaymentDLQEvent buildDLQEvent(PaymentRetryEvent retryEvent) {

        log.error("Sending to DLQ paymentId={} retryCount={}",
                retryEvent.getEvent().getPaymentId(),
                retryEvent.getRetryCount());

        return new com.example.events.PaymentDLQEvent(retryEvent);
    }
}