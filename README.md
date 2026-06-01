# Dolsk Tyre Department

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

 A web platform that enables users (especially e-hailing drivers and fleets) to purchase affordable tyres online and access tyre insurance for long-term cost protection.
---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Run](#setup--run)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- Online tyre catalog and sales
- Tyre insurance management
- Order processing and tracking
- Cart management for e-hailing drivers
- User authentication and role-based access
- Payment tracking (optional integration)
- Global exception handling

---

## Tech Stack

- **Backend:** Java Spring Boot
- **Database:** PostgreSQL
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **Version Control:** Git/GitHub

---

## Project Structure

```text
src/
├─ main/
│  ├─ java/com/dolsk/tyres/
│  │  ├─ config/             # Security & service configuration
│  │  ├─ controller/         # REST API controllers
│  │  ├─ dto/                # Data transfer objects
│  │  ├─ exception/          # Custom exceptions & handlers
│  │  ├─ model/              # Entity models
│  │  ├─ repository/         # JPA repositories
│  │  ├─ security/           # JWT & authentication filters
│  │  ├─ service/impl/       # Service implementations
│  │  ├─ service/service/    # Service interfaces
│  │  ├─ util/               # Utility classes (e.g., PasswordHelper)
│  │  └─ DolskTyreApplication.java
│  └─ resources/
│     ├─ application.properties
│     └─ data.sql
├─ test/                      # Unit & integration tests
Setup & Run
Clone the repo:

bash
Copy code
git clone https://github.com/fiskhumalo/DolskTyreDepartment.git
cd dolsk-tyre-backend
Configure PostgreSQL database in application.properties.

Build the project using Maven:

bash
Copy code
mvn clean install
Run the Spring Boot application:

bash
Copy code
mvn spring-boot:run
Access APIs at: http://localhost:8080/api/...

API Endpoints (Overview)
Auth
POST /api/auth/signup - Register user

POST /api/auth/login - Login user

Tyres
GET /api/tyres - Get all tyres

POST /api/tyres - Add a new tyre

PUT /api/tyres/{id} - Update tyre

DELETE /api/tyres/{id} - Delete tyre

Orders
POST /api/orders - Place order

GET /api/orders/{id} - Get order by ID

Cart
POST /api/cart/{userId}/add - Add tyre to cart

GET /api/cart/{userId} - Get cart

DELETE /api/cart/{userId}/clear - Clear cart

Full API documentation should be added separately (Swagger recommended).

Contributing
Fork the repository

Create your feature branch: git checkout -b feature/my-feature

Commit your changes: git commit -m "Add some feature"

Push to branch: git push origin feature/my-feature

Open a Pull Request

License
This project is licensed under the MIT License. See the LICENSE file for details.


