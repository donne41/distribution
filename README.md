# MicroChat

![GitHub stars](https://img.shields.io/github/stars/donne41/distribution?style=for-the-badge&logo=github) ![GitHub forks](https://img.shields.io/github/forks/donne41/distribution?style=for-the-badge&logo=github) ![GitHub issues](https://img.shields.io/github/issues/donne41/distribution?style=for-the-badge&logo=github) ![Last commit](https://img.shields.io/github/last-commit/donne41/distribution?style=for-the-badge&logo=github) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![Java (Maven)](https://img.shields.io/badge/Java%20(Maven)-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

## 📑 Table of Contents

- [Description](#description)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Key Dependencies](#key-dependencies)
- [Project Structure](#project-structure)
- [Development Setup](#development-setup)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 📝 Description

Test of distirbution with microservice architecture so that each service is a standalone container and communicate with either REST calls or gRPC.

## 🛠️ Tech Stack

- 🐳 **Docker**
- ☕ **Java (Maven)**

## ⚡ Quick Start

```bash

# 1. Clone the repository
git clone https://github.com/donne41/distribution.git

# 2. Replace API key
Enable bot service by getting a personal API key from example https://openrouter.ai/ 
Next in the .env file, uncomment the three AI_API lines and replace "YOUR_SECRET_KEY" with your api key

# 3. Run Docker
run the docker compose file with docker compose up --build -d

4. Start chatting
```

## Screenshots

![Login page](/screenshots/LoginPageMicrochat.jpg?raw=true "Login Page")

![Chat example](/screenshots/chatExampleMicrochat.jpg?raw=true "Chat window")

## 📦 Key Dependencies

```
spring-boot-starter-security: managed
spring-boot-starter-security-oauth2-authorization-server: managed
spring-boot-starter-security-oauth2-authorization-server-test: managed
spring-boot-starter-security-test: managed
spring-boot-starter-thymeleaf: managed
thymeleaf-extras-springsecurity6: managed
spring-boot-devtools: managed
spring-boot-starter-restclient: managed
```

## 📁 Project Structure

```
.
├── authservice
│   ├── .mvn
│   │   └── wrapper
│   │       └── maven-wrapper.properties
│   ├── Dockerfile
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── example
│       │   │       └── ...
│       │   └── resources
│       │       ├── application.properties
│       │       ├── static
│       │       │   └── ...
│       │       └── templates
│       │           └── ...
│       └── test
│           └── java
│               └── example
│                   └── ...
├── bff
│   ├── .mvn
│   │   └── wrapper
│   │       └── maven-wrapper.properties
│   ├── Dockerfile
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── example
│       │   │       └── ...
│       │   └── resources
│       │       ├── application.properties
│       │       ├── static
│       │       │   └── ...
│       │       └── templates
│       │           └── ...
│       └── test
│           └── java
│               └── example
│                   └── ...
├── botservice
│   ├── .mvn
│   │   └── wrapper
│   │       └── maven-wrapper.properties
│   ├── Dockerfile
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── ...
│           └── resources
│               └── application.properties
├── compose.yaml
├── distribution.iml
├── init-db
│   └── init.sql
├── messageservice
│   ├── .mvn
│   │   └── wrapper
│   │       └── maven-wrapper.properties
│   ├── Dockerfile
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── ...
│           ├── proto
│           │   └── message-service.proto
│           └── resources
│               └── application.properties
└── userservice
    ├── .mvn
    │   └── wrapper
    │       └── maven-wrapper.properties
    ├── Dockerfile
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src
        └── main
            ├── java
            │   └── example
            │       └── ...
            ├── proto
            │   └── user-service.proto
            └── resources
                ├── application.properties
                └── static
                    └── ...
```
