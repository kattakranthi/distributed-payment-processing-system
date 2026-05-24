# Payment API Design

## Problem Statement

The system should provide APIs to create and retrieve payments.

The system should support scalable payment processing and future distributed architecture.

---

## Functional Requirements

1. Create payment
2. Retrieve payment
3. Validate requests
4. Track payment status
5. Generate payment ID

---

## Non Functional Requirements

1. Scalable
2. Highly available
3. Extensible
4. Fault tolerant
5. Low latency

---

## APIs

### Create Payment

POST /api/v1/payments

Request

{
"amount": 500.00,
"currency": "USD",
"payerId": "USER123",
"payeeId": "MERCHANT456"
}

Response

{
"paymentId": "PAY12345",
"status": "PENDING"
}

### Get Payment

GET /api/v1/payments/{paymentId}

---

## Future Enhancements

1. Database persistence
2. Idempotency support
3. Kafka integration
4. Fraud checks
5. Retry mechanism