package com.example.paymentservice.producer;

import com.example.paymentservice.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<
            String,
            PaymentCreatedEvent>
            kafkaTemplate;

    private static final String
            TOPIC =
            "payment-created-topic";

    public void publishPaymentEvent(
            PaymentCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getPaymentId(),
                event);

        System.out.println(
                "Payment event published: "
                        + event);
    }
}