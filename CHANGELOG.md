# Kapil Traders - Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- Added Spring Security dependencies for user authentication and authorization.
- Created `User` and `Role` entities for the security model.
- Implemented `UserRepository` and `RoleRepository` for data access.
- Added `SecurityConfig` for web security configuration.
- Implemented `CustomUserDetailsService` to load user data.
- Created `DataInitializer` to seed the database with default users and roles.
- Added a custom `login.html` page and `AuthController`.

### Changed
- Modified `products.html`, `index.html`, and `dashboard.html` to integrate Spring Security:
  - Added Thymeleaf Security Dialect.
  - Implemented logout functionality.
  - Conditionally rendered elements (e.g., 'Add Product', 'Edit', 'Delete' buttons) based on user roles (e.g., `ROLE_ADMIN`).
  - Updated welcome messages to display authenticated username.
- Refined `SecurityConfig` to include granular URL-based authorization (e.g., `/add-product` requires `ROLE_ADMIN`). 

### Changed
-

---

## [1.0.0] - 2025-08-15

### Added
- **Initial Application Setup:**
  - Spring Boot (v3.2.0) project initialized with Maven.
  - Java 17 as the core language.
- **Core Features:**
  - CRUD functionality for Products, Customers, and Sales.
  - Basic inventory management logic.
- **Frontend:**
  - Server-side rendered UI using Thymeleaf.
  - HTML templates for all major views (`index`, `products`, `sales`, etc.).
- **Backend & Data:**
  - Spring Web for handling HTTP requests.
  - Spring Data JPA for database interaction.
  - PostgreSQL database integration.
  - Configuration for static file uploads (product images).
- **Reporting:**
  - iTextPDF dependency included for future PDF generation.
