# observability-lab

Spring Boot observability lab focused on metrics, tracing, and monitoring using Micrometer, Prometheus, Grafana, and OpenTelemetry.

## Overview

This project is a hands-on lab to experiment with observability patterns in Spring Boot applications.

It explores how telemetry is produced, propagated, collected, and visualized across a typical monitoring stack, with emphasis on practical behavior rather than theoretical definitions.

## Scope

The lab covers:

* Application metrics via Micrometer and Spring Boot Actuator
* Metrics scraping and storage with Prometheus
* Visualization and dashboards in Grafana
* Distributed tracing concepts using OpenTelemetry and Grafana Tempo
* End-to-end local setup using Docker Compose

## Tech stack

* Java 21
* Spring Boot 4
* Spring Boot Actuator
* Micrometer
* Prometheus
* Grafana
* OpenTelemetry
* Grafana Tempo
* Docker Compose
* Maven

## Setup

### 1. Start observability stack

```bash
docker compose up -d
```

### 2. Run the application

```bash id="runapp01"
./mvnw spring-boot:run
```

### 3. Access

* Application: http://localhost:8080
* Prometheus: http://localhost:9090
* Grafana: http://localhost:3000

## Configuration highlights

* Metrics exposed via `/actuator/prometheus`
* Prometheus configured to scrape application metrics
* Tracing exported via OTLP (HTTP)
* Sampling set to `1.0` for full trace visibility during experimentation
* Metrics export via OTLP disabled (Prometheus used as primary metrics backend)

## What this lab explores

* Behavior of Micrometer metrics under load and failure scenarios
* Impact of tagging strategies and cardinality
* Trace structure (spans, hierarchy, propagation)
* Correlation between metrics and traces
* Observability signal flow across the stack

## Notes

* Focused on local experimentation and iteration
* Configuration intentionally simplified
* Not production-oriented