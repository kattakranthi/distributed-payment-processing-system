package com.example.paymentservice.producer;

import com.example.paymentservice.config.KafkaTopics;
import com.example.events.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(PaymentEventProducer.class);

    // =========================
    // MAIN EVENT
    // =========================
    public void sendPaymentEvent(PaymentCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_CREATED_TOPIC,
                event.getPaymentId().toString(),
                event
        );

        log.info("Payment event published paymentId={}", event.getPaymentId());
    }

    // =========================
    // RETRY EVENT
    // =========================
    public void sendToRetry(PaymentRetryEvent retryEvent) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_RETRY_TOPIC,
                retryEvent.getEvent().getPaymentId().toString(),
                retryEvent
        );

        log.info("Retry event published paymentId={} retryCount={}",
                retryEvent.getEvent().getPaymentId(),
                retryEvent.getRetryCount());
    }

    // =========================
    // DLQ
    // =========================
    public void sendToDLQ(PaymentDLQEvent dlqEvent) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_DLQ_TOPIC,
                dlqEvent.getEvent().getPaymentId().toString(),
                dlqEvent
        );

        log.error("DLQ event published paymentId={} reason={}",
                dlqEvent.getEvent().getPaymentId(),
                dlqEvent.getErrorMessage());
    }

    // =========================
    // COMPLETED EVENT
    // =========================
    public void publishPaymentCompleted(PaymentCompletedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_COMPLETED_TOPIC,
                event.getPaymentId(),
                event
        );

        log.info("Payment completed published paymentId={}", event.getPaymentId());
    }
}