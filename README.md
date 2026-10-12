# MTANAV — Location-Based Discovery & Local Commerce Platform

MTANAV is a location-based discovery and local commerce platform allowing users to find **specific products and clearly defined services**, view the businesses that provide them on a map, inspect exact products/services offered by a business, view prices and bundle options, place online orders or service bookings, and navigate directly to business coordinates using Google Maps.

---

## Key Architectural Feature: Dynamic Map Filtering

MTANAV does **NOT** show irrelevant businesses on the map when a product or service is selected.
- Selecting **🥄 Spoon** queries `/api/businesses/map?productId=1` (or `?query=Spoon`).
- The backend filters and returns **ONLY** businesses that currently sell **Spoon**.
- Selecting **🍚 Rice** removes previous Spoon markers and displays **ONLY** businesses associated with Rice.
- Selecting **🔧 Garage** displays **ONLY** businesses providing garage automotive services.

---

## Tech Stack

### Frontend
- **Language**: Kotlin 1.9.23
- **Framework**: Compose Multiplatform (Android & Wasm/Web)
- **Architecture**: Clean Architecture (UI → StateHolder/ViewModel → Repository → Ktor API)
- **HTTP Client**: Ktor Client with Kotlinx Serialization
- **Navigation**: Platform-aware navigation (Google Maps Intent on Android, Web URL on Wasm/Web)

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.2.5
- **Build Tool**: Apache Maven (`pom.xml`)
- **Database**: PostgreSQL with Flyway Migrations
- **Security**: Spring Security + JWT Authentication + BCrypt
- **API Doc**: Springdoc OpenAPI / Swagger (`/swagger-ui.html`)

---

## Database Migrations & Seed Data

- `V1__init_schema.sql`: Full schema including `users`, `roles`, `service_categories`, `product_categories`, `businesses`, `business_images`, `services`, `business_services`, `products`, `business_products`, `product_variants`, `orders`, `order_items`, `bookings`, `payments`, `reviews`, `favorites`, `audit_logs`.
- `V2__seed_data.sql`: Pre-populated seed data for Dar es Salaam demo businesses (Kariakoo, Mlimani City, Sinza, Mikocheni, Posta), quick products (Spoon, Rice, Phones, Chargers, Shoes, Clothes, Laptops, Furniture), services (Banking, Garage, Washing, Salon, Medical, Printing, Phone Repair, Barber, Car Wash, Tailoring), and product bundle options.

---

## Setup & Running Instructions

### 1. Database Setup
Ensure PostgreSQL is running locally on port 5432:
```bash
createdb mtanav
```

### 2. Backend Launch
Navigate to `backend/` and start Spring Boot:
```bash
cd backend
mvn spring-boot:run
```
Swagger API docs will be available at: `http://localhost:8080/swagger-ui.html`

### 3. Frontend Launch
Navigate to `frontend/`:
- **For Wasm/Web**:
```bash
cd frontend
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
- **For Android**:
```bash
cd frontend
./gradlew :composeApp:assembleDebug
```

---

## Core Filtering Verification Test

Backend integration test `BusinessFilteringIntegrationTest.java` verifies:
- Given Shop A sells Spoon, Shop B sells Rice, Shop C sells Spoon.
- When querying businesses for **Spoon**, Shop A and Shop C are returned.
- **Shop B is strictly excluded**.
