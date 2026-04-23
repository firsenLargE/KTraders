# Kapil Traders Inventory Management System

An enhanced inventory management system built with Spring Boot, featuring AI-powered forecasting, comprehensive reporting, and user authentication. This application helps manage products, sales, purchases, customers, and generates insights for better business decisions.

## Features

- **Product Management**: Add, edit, delete, and track products with categories, pricing, and stock levels
- **Sales & Purchase Tracking**: Record daily/monthly sales and purchases with detailed reporting
- **Customer Management**: Maintain customer information and order history
- **Order Processing**: Handle purchase orders with status tracking (Pending, Processing, Completed)
- **Inventory Forecasting**: AI-powered demand forecasting with seasonal analysis
- **Reporting**: Generate PDF reports for sales, purchases, and inventory
- **Dashboard**: Real-time overview of inventory value, low stock alerts, and recent activity
- **User Authentication**: Role-based access control (Admin/User roles)
- **File Uploads**: Support for product images and document uploads
- **Cash Ledger**: Track financial transactions and balances

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL (with JPA/Hibernate)
- **Frontend**: Thymeleaf templates with Bootstrap
- **Build Tool**: Maven
- **Additional Libraries**:
  - iTextPDF/OpenPDF for PDF generation
  - Apache Commons Math for statistical calculations
  - Chart.js for data visualization
  - Spring Security for authentication

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **PostgreSQL 12+** database server

## Database Setup

1. Create a PostgreSQL database named `kapiltradersdb`
2. Create a database user with appropriate permissions (default config uses `zion`/`zion`)
3. Update `src/main/resources/application.properties` if your database credentials differ:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kapiltradersdb
spring.datasource.username=your_username
spring.datasource.password=your_password
