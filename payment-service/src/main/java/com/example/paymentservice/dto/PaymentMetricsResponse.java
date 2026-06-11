package com.example.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMetricsResponse {

    private long successCount;
    private long failedCount;
    private long processingCount;
    private long totalPayments;
}