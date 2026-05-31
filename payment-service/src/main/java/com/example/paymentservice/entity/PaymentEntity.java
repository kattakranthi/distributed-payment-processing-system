package com.example.paymentservice.entity;

import com.example.paymentservice.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    private String paymentId;

    private BigDecimal amount;

    private String currency;

    private String payerId;

    private String payeeId;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime updatedAt;

    private LocalDateTime processedAt;
}