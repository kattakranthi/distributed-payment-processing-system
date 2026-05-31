package com.example.paymentservice.producer;

import com.example.paymentservice.config.KafkaTopics;
import com.example.paymentservice.event.PaymentCreatedEvent;
import com.example.paymentservice.event.PaymentRetryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    // =========================
    // MAIN EVENT
    // =========================
    public void sendPaymentEvent(PaymentCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_CREATED_TOPIC,
                event.getPaymentId(),
                event
        );

        System.out.println("Payment event published: " + event.getPaymentId());
    }

    // =========================
    // RETRY EVENT
    // =========================
    public void sendToRetry(PaymentRetryEvent retryEvent) {

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_RETRY_TOPIC,
                retryEvent.getEvent().getPaymentId(),
                retryEvent
        );

        System.out.println("Retry event published for: " +
                retryEvent.getEvent().getPaymentId());
    }

    // =========================
    // DLQ
    // =========================
    public void sendToDLQ(PaymentRetryEvent retryEvent, String reason) {

        System.out.println("Sending to DLQ due to: " + reason);

        kafkaTemplate.send(
                KafkaTopics.PAYMENT_DLQ_TOPIC,
                retryEvent.getEvent().getPaymentId(),
                retryEvent
        );
    }
}