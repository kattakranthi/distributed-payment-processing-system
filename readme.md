# Distributed Payment Processing System

## Overview

This project demonstrates a distributed, event-driven payment processing system built using **Spring Boot**, **Apache Kafka**, and **PostgreSQL**.

The system simulates a real-world payment processing architecture commonly used in financial institutions and payment platforms.

### Key Capabilities

* Payment event publishing
* Kafka-based asynchronous processing
* Retry handling for transient failures
* Dead Letter Queue (DLQ) for permanent failures
* Idempotency support to avoid duplicate payment processing
* Swagger UI for API testing
* Validation and global exception handling
* Observability using Spring Boot Actuator and metrics
* Dockerized deployment

---

## Architecture

```text
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
```

### Retry Flow

```text
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
```

---

## Technology Stack

* Java 17
* Spring Boot 3
* Spring Kafka
* Apache Kafka
* PostgreSQL
* Spring Data JPA
* Docker & Docker Compose
* Spring Boot Actuator
* Micrometer Metrics
* Lombok
* Swagger / OpenAPI
* Maven

---

## Features

### 1. Payment Event Processing

Payments are processed asynchronously using Kafka.

Instead of processing payments synchronously inside the API request, payment requests are converted into Kafka events and processed in the background.

### Benefits

* Better scalability
* Loose coupling
* Fault tolerance
* Resilience

---

### 2. Retry Mechanism

Temporary failures are retried automatically.

### Examples

* Database timeout
* Network issue
* Downstream API unavailable
* Temporary Kafka issues

### Flow

```text
payment-created-topic
       ↓ fail
payment-retry-topic
       ↓ retry
Success OR DLQ
```

### Maximum Retries

**3 retries**

---

### 3. Dead Letter Queue (DLQ)

Permanent failures are moved to a dedicated Kafka topic.

### Examples

* Invalid payload
* Corrupted message
* Fraud blocked transaction
* Unsupported currency
* Invalid business validation

### Topic

```text
payment-dlq-topic
```

### Purpose

* Avoid blocking message processing
* Preserve failed events
* Manual investigation

---

### 4. Idempotency

Idempotency prevents duplicate payment processing.

If the same request is submitted multiple times using the same idempotency key:

```text
Same request
Same idempotency key
        ↓
Payment processed only once
```

### Example Request

```json
{
  "amount": 100,
  "currency": "USD",
  "payerId": "USER-1",
  "payeeId": "MERCHANT-1",
  "idempotencyKey": "payment-1001"
}
```

### Result

```text
Duplicate request ignored
```

---

### 5. Observability

The system includes observability support for monitoring and troubleshooting.

### Included Features

* Structured logging
* Health checks
* Application metrics
* Kafka monitoring
* Retry and failure visibility

### Actuator Endpoints

```bash
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/prometheus
```

---

## Kafka Topics

| Topic Name            | Purpose                 |
| --------------------- | ----------------------- |
| payment-created-topic | Main payment processing |
| payment-retry-topic   | Retry failed events     |
| payment-dlq-topic     | Dead Letter Queue       |

---

## Project Structure

```text
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
```

---

# Running the Project

## Option 1: Run Using Docker (Recommended)

### Prerequisites

Install:

* Docker Desktop
* Docker Compose

### Start All Services

Run:

```bash
docker-compose up -d
```

This will start:

* PostgreSQL
* Zookeeper
* Kafka
* Payment Service

### Verify Containers

```bash
docker ps
```

Expected containers:

```text
postgres
zookeeper
kafka
payment-service
```

### View Logs

```bash
docker logs payment-service
```

### Stop Containers

```bash
docker-compose down
```

---

## Docker Configuration

### Build Application Docker Image

```bash
mvn clean package
```

Build image:

```bash
docker build -t payment-service .
```

Run container:

```bash
docker run -p 8080:8080 payment-service
```

---

## Sample docker-compose.yml

```yaml
version: '3.8'

services:

  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      POSTGRES_DB: paymentdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  payment-service:
    build: .
    container_name: payment-service
    depends_on:
      - postgres
      - kafka
    ports:
      - "8080:8080"
```

---

## Option 2: Run Locally

### Step 1: Start Zookeeper

```bash
.\zookeeper-server-start.bat ..\..\config\zookeeper.properties
```

### Step 2: Start Kafka

```bash
.\kafka-server-start.bat ..\..\config\server.properties
```

### Step 3: Create Kafka Topics

Create payment topic:

```bash
.\kafka-topics.bat --create --topic payment-created-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

Create retry topic:

```bash
.\kafka-topics.bat --create --topic payment-retry-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

Create DLQ topic:

```bash
.\kafka-topics.bat --create --topic payment-dlq-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

Verify topics:

```bash
.\kafka-topics.bat --list --bootstrap-server localhost:9092
```

Expected:

```text
payment-created-topic
payment-retry-topic
payment-dlq-topic
```

### Step 4: Run Application

```bash
mvn spring-boot:run
```

Or run:

```text
PaymentServiceApplication.java
```

---

## Swagger UI

Open:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## API Documentation

### Create Payment

**Endpoint**

```http
POST /payments
```

### Validation Rules

| Field          | Validation             |
| -------------- | ---------------------- |
| amount         | Not null, minimum 0.01 |
| currency       | Required               |
| payerId        | Required               |
| payeeId        | Required               |
| idempotencyKey | Required               |

### Success Request Example

```json
{
  "amount": 100.50,
  "currency": "USD",
  "payerId": "USER-1001",
  "payeeId": "MERCHANT-2001",
  "idempotencyKey": "idem-payment-1001"
}
```

### Response

```json
{
  "status": "SUCCESS",
  "message": "Payment accepted"
}
```

---

## Retry Example

To simulate retry:

```java
if ("RETRY-USER".equals(event.getPayerId())) {
    throw new RuntimeException("Simulated retry");
}
```

---

## DLQ Example

```java
if ("DLQ-USER".equals(event.getPayerId())) {
    throw new RuntimeException("Permanent failure");
}
```

Verify DLQ:

```bash
.\kafka-console-consumer.bat --topic payment-dlq-topic --bootstrap-server localhost:9092 --from-beginning
```

---

## Future Enhancements

* Kubernetes deployment
* Prometheus + Grafana monitoring
* Redis caching
* Multi-service communication
* Authentication & authorization
* CI/CD pipeline with GitHub Actions
