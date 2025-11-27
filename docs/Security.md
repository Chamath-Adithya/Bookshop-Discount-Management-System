# 🛡️ Project Security Features & Strategies

This document outlines the comprehensive security measures implemented in the **Bookshop Discount Management System (BDMS)** to protect user data, ensure system integrity, and prevent common vulnerabilities.

---

## 1️⃣ Authentication & Password Security

### 🔒 Strategy: Strong Hashing with BCrypt
We have moved away from storing passwords in plain text. Instead, we utilize the **BCrypt** hashing algorithm.

*   **Why BCrypt?**: BCrypt is a password-hashing function designed to be slow, making it resistant to brute-force search attacks. It also automatically handles **salting**, which prevents rainbow table attacks.
*   **Implementation**:
    *   **Dependency**: `org.mindrot:jbcrypt:0.4`
    *   **Registration**: When a new user is created (e.g., by the Manager), their password is hashed before being written to `users.csv`.
    *   **Login**: When a user attempts to log in, the system hashes their input and compares it to the stored hash using `BCrypt.checkpw()`.

```java
// Example of secure verification
if (BCrypt.checkpw(inputPassword, storedHash)) {
    // Access Granted
}
```

---

## 2️⃣ Data Integrity & Concurrency Control

### 🔒 Strategy: File Locking
Since the system uses CSV files for data storage, there is a risk of data corruption if multiple processes or threads attempt to write to the same file simultaneously (Race Conditions).

*   **Solution**: We implemented **File Locking** using Java's NIO `FileChannel`.
*   **Implementation**:
    *   Before writing to any CSV file, the `FileHandler` acquires an exclusive lock on the file.
    *   This ensures that only one write operation can happen at a time.
    *   The lock is released immediately after the write is complete.

```java
try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
     FileLock lock = channel.lock()) { // Acquire exclusive lock
    // Safe to write data
}
```

---

## 3️⃣ Input Validation & Sanitization

### 🔒 Strategy: CSV Injection Prevention
Allowing user input to be written directly to a CSV file can lead to **CSV Injection** (or Formula Injection) attacks. If a malicious user enters a name like `=cmd|' /C calc'!A0`, opening the CSV in Excel could execute that command.

*   **Solution**: We sanitize all user inputs before writing them to the database.
*   **Implementation**:
    *   The `FileHandler.escapeCsvField()` method checks if a field starts with risky characters (`=`, `+`, `-`, `@`).
    *   If found, it prepends a single quote (`'`) to neutralize the formula.

### 🔒 Strategy: Strict Data Validation
To prevent malformed data from entering the system, we enforce strict validation rules.

*   **Customer Names**: Must not be empty.
*   **Phone Numbers**: Must be exactly 10 digits (`^\d{10}$`).
*   **Prices**: Must be non-negative.

---

## 4️⃣ Access Control

### 🔒 Strategy: Role-Based Access Control (RBAC)
The system distinguishes between different types of users to restrict access to sensitive functionality.

*   **Manager**: Has full access, including the ability to create new users and manage products.
*   **Cashier**: Has restricted access, limited to processing sales and viewing products.
*   **Implementation**: The `User` model has a `role` field. The UI adapts based on the logged-in user's role, hiding unauthorized buttons and views.

---
