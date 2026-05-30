# Distributed Payment Processing System

## Overview

This project demonstrates a distributed, event-driven payment processing system built using Spring Boot, Apache Kafka, and PostgreSQL.

The system simulates real-world payment processing architecture used in financial institutions and payment platforms, supporting:

- Payment event publishing
- Kafka-based asynchronous processing
- Retry handling for transient failures
- Dead Letter Queue (DLQ) for permanent failures
- Idempotency support to avoid duplicate payment processing
- Swagger UI for API testing
- Validation and exception handling

---

## Architecture


Client Request
      ↓
REST API (Spring Boot)
      ↓
Payment Service
      ↓
Kafka Producer
      ↓
payment-created-topic
      ↓
Kafka Consumer
      ↓
Payment Processing
      ↓
SUCCESS / RETRY / DLQ

##Retry Flow
Payment Consumer Failure
          ↓
payment-retry-topic
          ↓
Retry Attempt 1
          ↓
Retry Attempt 2
          ↓
Retry Attempt 3
          ↓
payment-dlq-topic

Technology Stack
Java 17
Spring Boot 3
Spring Kafka
Apache Kafka
PostgreSQL
Spring Data JPA
Lombok
Swagger / OpenAPI
Maven

Features
1. Payment Event Processing

Payments are processed asynchronously using Kafka.

Instead of processing payments synchronously inside the API request, payment requests are converted into Kafka events and processed in the background.

Benefits:

Better scalability
Loose coupling
Fault tolerance
Resilience
2. Retry Mechanism

Temporary failures are retried automatically.

Examples:

Database timeout
Network issue
Downstream API unavailable
Temporary Kafka issues

Flow:

payment-created-topic
       ↓ fail
payment-retry-topic
       ↓ retry
Success OR DLQ

Maximum retries:

3 retries
3. Dead Letter Queue (DLQ)

Permanent failures are moved to a dedicated Kafka topic.

Examples:

Invalid payload
Corrupted message
Fraud blocked transaction
Unsupported currency
Invalid business validation

Topic:

payment-dlq-topic

Purpose:

Avoid blocking message processing
Preserve failed events
Manual investigation
4. Idempotency

Idempotency prevents duplicate payment processing.

If the same request is submitted multiple times using the same idempotency key:

Same request
Same idempotency key
        ↓
Payment processed only once

Example:

Request 1:

{
  "amount": 100,
  "currency": "USD",
  "payerId": "USER-1",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "payment-1001"
}

Request 2 (same request):

{
  "amount": 100,
  "currency": "USD",
  "payerId": "USER-1",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "payment-1001"
}

Result:

Duplicate request ignored
Kafka Topics
Topic Name	Purpose
payment-created-topic	Main payment processing
payment-retry-topic	Retry failed events
payment-dlq-topic	Dead letter queue
Project Structure
src/main/java/com/example/paymentservice
│
├── controller
│   └── PaymentController
│
├── service
│   └── PaymentServiceImpl
│
├── consumer
│   └── PaymentEventConsumer
│
├── producer
│   └── PaymentEventProducer
│
├── repository
│   └── PaymentRepository
│
├── entity
│   └── PaymentEntity
│
├── event
│   ├── PaymentCreatedEvent
│   └── PaymentRetryEvent
│
├── dto
│   ├── PaymentRequest
│   └── PaymentResponse
│
├── config
│   └── KafkaTopics
│
└── exception
    └── GlobalExceptionHandler
Setup Instructions
Step 1: Start Zookeeper
.\zookeeper-server-start.bat ..\..\config\zookeeper.properties
Step 2: Start Kafka
.\kafka-server-start.bat ..\..\config\server.properties
Step 3: Create Kafka Topics

Create payment topic:

.\kafka-topics.bat --create --topic payment-created-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

Create retry topic:

.\kafka-topics.bat --create --topic payment-retry-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

Create DLQ topic:

.\kafka-topics.bat --create --topic payment-dlq-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

Verify topics:

.\kafka-topics.bat --list --bootstrap-server localhost:9092

Expected:

payment-created-topic
payment-retry-topic
payment-dlq-topic
Step 4: Run Application
mvn spring-boot:run

Or run:

PaymentServiceApplication.java
Swagger UI

Open:

http://localhost:8080/swagger-ui/index.html
API Documentation
Create Payment

Endpoint:

POST /payments
Request Validation

Fields:

Field	Validation
amount	Not null, minimum 0.01
currency	Required
payerId	Required
payeeId	Required
idempotencyKey	Required
Swagger Request Examples
Success Example
{
  "amount": 100.50,
  "currency": "USD",
  "payerId": "USER-1001",
  "payeeId": "MERCHANT-2001",
  "idempotencyKey": "idem-payment-1001"
}

Expected:

{
  "status": "SUCCESS",
  "message": "Payment accepted"
}
Retry Example

To simulate retry:

Temporary code:

if ("RETRY-USER".equals(event.getPayerId())) {
    throw new RuntimeException("Simulated retry");
}

Request:

{
  "amount": 200,
  "currency": "USD",
  "payerId": "RETRY-USER",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "retry-1001"
}

Flow:

payment-created-topic
      ↓ fail
payment-retry-topic
DLQ Example

Temporary code:

if ("DLQ-USER".equals(event.getPayerId())) {
    throw new RuntimeException("Permanent failure");
}

Request:

{
  "amount": 300,
  "currency": "USD",
  "payerId": "DLQ-USER",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "dlq-1001"
}

Flow:

Retry 1 ❌
Retry 2 ❌
Retry 3 ❌
      ↓
payment-dlq-topic

Verify DLQ:

.\kafka-console-consumer.bat --topic payment-dlq-topic --bootstrap-server localhost:9092 --from-beginning
Idempotency Example

Request:

{
  "amount": 100,
  "currency": "USD",
  "payerId": "USER-1",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "same-request-1001"
}

Sending same request again:

Expected:

Duplicate request detected
