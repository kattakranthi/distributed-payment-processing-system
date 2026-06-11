package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.dto.PaymentStatusUpdateRequest;
import com.example.paymentservice.entity.PaymentEntity;

import java.util.List;
public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPayment(String paymentId);

    PaymentResponse updatePaymentStatus(
            String paymentId,
            PaymentStatusUpdateRequest request);

    List<PaymentResponse> getPayments();

}