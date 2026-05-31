package com.example.notification.consumer;


import com.example.events.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentNotificationConsumer {

    @KafkaListener(
            topics = "payment-completed-topic",
            groupId = "notification-group"
    )
    public void consume(PaymentCompletedEvent event) {

        log.info("Notification received for payment: {}", event.getPaymentId());

        sendEmail(event);
        sendSms(event);
    }

    private void sendEmail(PaymentCompletedEvent event) {
        System.out.println("EMAIL → Payment successful: " + event.getPaymentId());
    }

    private void sendSms(PaymentCompletedEvent event) {
        System.out.println("SMS → Payment done for " + event.getPayerId());
    }
}
