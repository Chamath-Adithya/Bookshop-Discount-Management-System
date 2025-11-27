# 📘 Bookshop Discount Management System (BDMS)

> **University Object-Oriented Programming (OOP) Assignment**
>
> *A comprehensive JavaFX application demonstrating core OOP principles, secure data handling, and modern UI design.*

---

## 🧠 Project Overview

The **Bookshop Discount Management System (BDMS)** is a robust desktop application designed to solve a real-world business challenge: managing complex, multi-tiered discount structures in a retail environment. Built with **Java 11**, **JavaFX 17**, and **Maven**, this project serves as a practical demonstration of **Object-Oriented Programming (OOP)** concepts, software architecture, and secure coding practices.

### ❓ The Problem
Bookshops often struggle with manual discount calculations, leading to:
*   **Human Error**: Mistakes in calculating bulk discounts or VIP rates.
*   **Inefficiency**: Slow checkout processes.
*   **Inconsistency**: Different discounts applied to similar customers.
*   **Security Risks**: Unsecured data storage and weak authentication.

### ✅ The Solution
BDMS automates the entire process by:
1.  **Dynamic Pricing**: Automatically applying quantity-based bulk discounts.
2.  **Customer Tiers**: Distinguishing between **Regular** and **VIP** customers (VIPs get an extra 5% off).
3.  **Secure Access**: Role-based login (Manager vs. Cashier) with **BCrypt password hashing**.
4.  **Data Integrity**: Using file locking to prevent data corruption in CSV storage.

---

## ⚙️ Key Features

### 🔐 Security & Reliability
*   **BCrypt Hashing**: User passwords are never stored in plain text. They are hashed using the industry-standard BCrypt algorithm.
*   **Input Sanitization**: All user inputs are sanitized to prevent **CSV Injection** attacks.
*   **Concurrency Control**: Implemented **File Locking** (`FileChannel.lock`) to ensure safe concurrent writes to data files.
*   **Strict Validation**: Robust validation for customer data (e.g., phone number format).

### 💻 Modern User Interface
*   **Cashier Portal**: A responsive, grid-based POS interface with real-time product filtering.
*   **Auto-Search**: Instant product and customer lookup without manual "Search" buttons.
*   **Live Cart**: Dynamic shopping cart with real-time subtotal and discount updates.

### 🛠 Architecture
*   **MVC Pattern**: Separation of concerns using Model-View-Controller architecture.
*   **Service Layer**: Business logic is encapsulated in `Service` classes (`AuthService`, `ProductService`, `CustomerService`).
*   **File-Based Persistence**: Lightweight CSV storage (`users.csv`, `products.csv`, `customers.csv`) making the app portable and database-free.

---

## 📚 OOP Principles Demonstrated

This project is built upon the four pillars of Object-Oriented Programming:

### 1️⃣ Encapsulation
Data is hidden within classes and accessed only through public methods, ensuring data integrity.

```java
public class Product {
    private String productId;
    private double realPrice;
    
    // Controlled access via getters and setters
    public double getRealPrice() { return realPrice; }
    public void setRealPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.realPrice = price;
    }
}
```

### 2️⃣ Inheritance
Specialized classes inherit behavior from generalized classes to promote code reuse.

```java
// Base class
public abstract class Customer {
    protected String name;
    public abstract double getBaseDiscountRate();
}

// Derived class
public class VIPCustomer extends Customer {
    @Override
    public double getBaseDiscountRate() {
        return 0.05; // VIPs get 5% base discount
    }
}
```

### 3️⃣ Polymorphism
The system treats different objects (Regular vs. VIP) uniformly but they behave differently at runtime.

```java
Customer customer = new VIPCustomer("John");
// The system doesn't need to know it's a VIP; it just asks for the rate.
double discount = total * customer.getBaseDiscountRate(); 
```

### 4️⃣ Abstraction
Complex logic is hidden behind simple interfaces. The `FileHandler` utility abstracts away the low-level details of file I/O, locking, and parsing.

```java
// High-level usage
FileHandler.writeCsv("data/products.csv", lines);

// Low-level implementation (hidden)
// - Opens FileChannel
// - Acquires FileLock
// - Writes ByteBuffers
// - Handles IOExceptions
```

---

## 📂 Project Structure

```
BookshopDiscountSystem/
├── src/main/java/bookshop/
│   ├── controllers/      # UI Logic (CashierController, AdminController)
│   ├── model/            # Data Classes (Product, Customer, User)
│   ├── service/          # Business Logic (AuthService, ProductService)
│   └── util/             # Helpers (FileHandler, PasswordMigrator)
├── src/main/resources/
│   ├── FXML/             # UI Layouts (.fxml)
│   └── Styles/           # CSS Styling
├── data/                 # CSV Data Storage
├── docs/                 # Documentation & Security Audit
└── pom.xml               # Maven Dependencies
```

---

## 🚀 Installation & Setup

### Prerequisites
*   **Java JDK 11** or higher.
*   **Maven** 3.6+.

### Steps to Run
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Chamath-Adithya/Bookshop-Discount-Management-System.git
    cd BookshopDiscountSystem
    ```

2.  **Build the Project**:
    ```bash
    mvn clean install
    ```

3.  **Run the Application**:
    ```bash
    mvn javafx:run
    ```

### Default Credentials
*   **Admin/Manager**: Creates new users.
*   **Worker/Cashier**: Handles sales.
*   *(Note: Default users are stored in `data/users.csv` with hashed passwords)*

---

## 🧪 Usage Scenarios

### Scenario 1: VIP Checkout
1.  Cashier logs in.
2.  Searches for "Pen" (Price: Rs.100).
3.  Adds 10 Pens (Bulk discount applies -> Rs.80/each).
4.  Selects Customer "John" (VIP).
5.  **System Calculation**:
    *   Subtotal: 10 * 100 = Rs.1000
    *   Bulk Price: 10 * 80 = Rs.800
    *   VIP Discount: 5% of Rs.800 = Rs.40
    *   **Final Total**: Rs.760

### Scenario 2: Security Check
1.  Malicious user tries to edit `customers.csv` to inject a spreadsheet formula (`=cmd|...`).
2.  **System Defense**: The application detects the special character and sanitizes the input by prepending a `'`, rendering the formula harmless.

---

## 👨‍💻 Contributors
*   **Chamath-Adithya** - *Lead Developer, Core Models & Project Foundation*
*   **yasaruhashen** - *Customer Hierarchy & Service*
*   **prabandisathsarani080-ai** - *User Roles & Data Management Services*
*   **IOICybErIOI** - *Main Application & Billing Logic*
*   **kaveesha-tharindi5** - *Validation, Exceptions & Testing*

---
*This project was developed for the University Object-Oriented Programming Module.*
