# Local Password Manager

## Project Description

Local Password Manager is a secure and robust web application designed to manage and store credentials (usernames and passwords) locally. Unlike cloud-based password managers, this solution ensures that your data remains under your total control, stored in an encrypted local database.

The system allows you to organize credentials by websites, filter searches, and manage multiple users, ensuring that each user only has access to their own information.

## Technologies Used

This project uses a modern and reliable technology stack to guarantee security, performance, and maintainability:

*   **Java 21**: The latest LTS version of the language, providing performance and security improvements.
*   **Spring Boot 3.3.5**: Leading framework for Java application development. Several modules are used:
    *   `spring-boot-starter-web`: To create the web application and REST APIs.
    *   `spring-boot-starter-security`: For robust authentication and authorization.
    *   `spring-boot-starter-data-jpa`: For data persistence using Hibernate.
    *   `spring-boot-starter-thymeleaf`: For server-side view rendering.
*   **SQLite**: Lightweight and self-contained database (`passwords.db`), ideal for storing data locally without needing complex database server configurations.
*   **Hibernate ORM**: For Object-Relational Mapping (ORM), facilitating database interaction.
*   **Thymeleaf + Thymeleaf Extras Spring Security 6**: Modern template engine for generating dynamic HTML and integrating security into views.
*   **Bootstrap / CSS**: (Implicit) For a responsive and user-friendly user interface design.
*   **Bucket4j 8.10.1**: Powerful library for implementing Rate Limiting to protect against brute-force attacks.
*   **Lombok**: To reduce boilerplate code (getters, setters, constructors) and keep the code clean.
*   **Maven**: Project and dependency management tool.

## Key Features

### 1. User Management & Authentication
*   **User Registration**: New users can register securely in the system.
*   **Login**: Secure form-based authentication system.
*   **Session Control**: Automatic user session management.
*   **Data Isolation**: Strict guarantee that a user can only view, edit, and delete their own credentials.

### 2. Credential Management (Vault)
*   **Create Credentials**: Store username and password for any website.
*   **Organization by Websites**: Credentials are automatically grouped under the corresponding website.
*   **Secure Viewing**: Passwords are stored encrypted and are only decrypted at the moment of viewing by the owner.
*   **Filtering and Search**: Ability to filter credentials within a website (e.g., search for manager user).
*   **Secure Deletion**: Users can delete their credentials, with security checks to prevent accidental deletion of others' data.

### 3. Website Management
*   **Editing**: Ability to edit website details (Name, URL).
*   **Automatic Detection**: When adding a credential for a new site, the site is automatically created.

### 4. Advanced Security
*   **Brute Force Protection (Rate Limiting)**: The system blocks suspicious behavior by limiting the number of requests allowed from a single IP address.
*   **IP Whitelist**: Configuration to allow unlimited access to trusted IP addresses (e.g., local home network), bypassing rate limits.
*   **Security Monitoring**: Internal log (`SecurityTracker`) of suspicious activities.
*   **HTTPS**: Configuration ready to use HTTPS (port 8443), ensuring that all communication between the browser and the server is encrypted.

## Why Is It Safe?

Security is the fundamental pillar of this project. Here we explain the layers of protection implemented:

### 1. Password Encryption (AES-256)
Your account passwords are **NEVER** stored in plain text. A robust symmetric encryption algorithm **AES (Advanced Encryption Standard)** is used.
*   This means that if someone steals the `passwords.db` database file, they will not be able to read your passwords without the master encryption key (`masterKey`).
*   Encryption and decryption occur on the server only when the authenticated user requests it.

### 2. User Password Hashing (BCrypt)
The password you use to log in to this application (`Local Password Manager`) is not stored even in encrypted form; it is stored **hashed** using **BCrypt** with a strength of 12.
*   **BCrypt** is an algorithm designed to be computationally slow, making it extremely difficult and costly for an attacker to try to guess passwords via brute force, even if they have access to the database.

### 3. Secure Communication (HTTPS)
The application is configured to use **HTTPS**.
*   It uses SSL/TLS certificates (PKCS12 Keystore) to encrypt the communication channel.
*   This prevents "Man-in-the-Middle" (MITM) attacks, where an attacker intercepts data (like your master password) as it travels across the network.

### 4. Network Protection (Rate Limiting and Whitelist)
To prevent bots or attackers from trying to guess passwords by testing millions of combinations:
*   **Rate Limiting**: **Bucket4j** is used to limit how many requests an IP can make per second/minute. If the limit is exceeded, the server responds with a `429 Too Many Requests` error and temporarily blocks the IP.
*   **IP Whitelist**: You can configure your trusted IPs in `application.properties` to avoid accidental blocks during intensive legitimate use.

### 5. Strict Authorization
The code implements security checks in every critical operation (such as deleting a password).
*   Before any action, the system verifies: `Is the user trying to delete this credential REALLY the owner of it?`. If not, the operation is blocked.

## Configuration (`application.properties`)

The main configuration file is located at `src/main/resources/application.properties`.

```properties
spring.application.name=local-password-manager

# Database Configuration (SQLite)
spring.datasource.url=jdbc:sqlite:passwords.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# Server and HTTPS Configuration
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=local-password-manager

# App Security Configuration
app.default.locale=es
# List of IPs separated by comma that bypass Rate Limiting
app.security.ip-whitelist=192.168.1.50,127.0.0.1
```

## How to Run

### Prerequisites
*   Java 21 installed.
*   Maven installed.

### Commands
1.  **Build the project**:
    ```bash
    mvn clean install
    ```
2.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
3.  **Access in browser**:
    Open `https://localhost:8443` (Accept the security warning if using a self-signed certificate).

---
*Developed with ❤️ and an obsessive focus on security.*
