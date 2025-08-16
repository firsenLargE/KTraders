# Project Summary: Kapil Traders

This document provides a summary of the Kapil Traders Inventory Management System project.

## Project Overview

- **Name:** KapilTraders
- **Description:** Kapil Traders Inventory Management System
- **Group ID:** com.example
- **Artifact ID:** KapilTraders
- **Version:** 1.0.0

## Core Technologies

- **Java Version:** 17
- **Framework:** Spring Boot (version 3.2.0)
- **Key Dependencies:**
    - `spring-boot-starter-web`: For building web applications, including RESTful APIs.
    - `spring-boot-starter-data-jpa`: For data persistence using the Java Persistence API.
    - `spring-boot-starter-thymeleaf`: For server-side Java template engine for web and standalone environments.
    - `postgresql`: PostgreSQL JDBC driver for database connectivity.
    - `itextpdf`: A library for creating and manipulating PDF documents.
    - `spring-boot-devtools`: Provides fast application restarts, LiveReload, and configurations for an enhanced development experience.

## Application Entry Point

The main application class is `com.example.demo.KapilTradersApplication`, which is a standard Spring Boot application.

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KapilTradersApplication {

    public static void main(String[] args) {
        SpringApplication.run(KapilTradersApplication.class, args);
    }
}
```

## Web Configuration

The `com.example.demo.WebConfig` class is used to configure web-related beans and settings. It specifically configures a resource handler to serve static files from the `uploads` directory.

```java
package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
```

This configuration means that any request to `/uploads/**` will be served from the `uploads/` directory in the project's root.
