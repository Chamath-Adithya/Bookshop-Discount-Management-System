# 📘 Bookshop Discount Management System (BDMS)

## 🧠 Project Idea

### **Overview**

The **Bookshop Discount Management System (BDMS)** is a JavaFX-based desktop application built using **Java 11**, **JavaFX 17**, and **Maven**. It solves a real-world business challenge — managing bookshop product discounts efficiently based on **quantity purchased** and **customer type**. The system clearly demonstrates all major **Object-Oriented Programming (OOP)** principles such as **Encapsulation**, **Inheritance**, **Abstraction**, and **Polymorphism**, implemented through a structured and modular design.

### **Problem Context**

Bookshops commonly offer discounts when customers purchase products in bulk. For example:

**Pen:**

* Quantity 1 → Rs.100 each
* Quantity 5 → Rs.95 each
* Quantity 10 → Rs.80 each

**Pencil:**

* Quantity 1 → Rs.40 each
* Quantity 3 → Rs.35 each
* Quantity 10 → Rs.30 each
* Quantity 100 → Rs.25 each

Managing such multi-tiered discount structures manually often leads to:

* **Errors** during manual calculations.
* **Inconsistent discounts** among customers.
* **Difficulty** in maintaining pricing rules.
* **Time consumption** during customer checkout.

Furthermore, bookshops often have **Regular** and **VIP** customers. VIP customers receive an additional discount (e.g., 5% off the final bill). This multi-level discount logic is complex to handle manually. Thus, the shop needs a **digital solution** that simplifies the process and reduces calculation errors.

### **Proposed Solution**

The BDMS provides a reliable and user-friendly platform for managing bookshop discounts. It allows:

* **Managers** to add products, set real prices, and define **quantity-based discounts**.
* **Workers** to calculate the total bill automatically for customers.
* **Regular and VIP Customers** to receive appropriate pricing and benefits.

The system automatically applies the best discount rule based on the entered quantity and adds an extra discount if the customer is VIP. The data (products, discounts, and customers) is stored in **CSV files**, ensuring that the system remains lightweight, portable, and database-free.

### **Goals and Objectives**

1. Automate **discount calculation** based on product quantity and customer type.
2. Implement and showcase all **OOP principles** using real-world logic.
3. Provide an interface for managers to manage product and discount information.
4. Simplify the billing process for workers through automatic calculation.
5. Reduce human errors and improve pricing consistency.
6. Ensure data persistence using file-based storage.
7. Demonstrate modular software design using Java packages and classes.

### **Expected Outcomes**

* Improved accuracy and efficiency in billing.
* Automated discount management.
* Demonstration of clean OOP-based system design.
* Ready-to-use Java application with real-world relevance.

---

## ⚙️ OOP Concepts Demonstrated

### 1️⃣ Encapsulation

Private fields with getters/setters ensure data protection and controlled access.

```java
public class Product {
    private String name;
    private double realPrice;
    private Map<Integer, Double> discountRules;

    public void setDiscount(int quantity, double price) {
        discountRules.put(quantity, price);
    }
}
```

### 2️⃣ Inheritance

The `VIPCustomer` inherits from `Customer` and overrides the discount calculation behavior.

```java
public class VIPCustomer extends Customer {
    private double vipDiscountRate = 0.05;

    @Override
    public double calculateFinalPrice(double amount) {
        return amount - (amount * vipDiscountRate);
    }
}
```

### 3️⃣ Polymorphism

Methods like `calculateFinalPrice()` behave differently based on object type.

```java
Customer regular = new RegularCustomer();
Customer vip = new VIPCustomer();

regular.calculateFinalPrice(1000); // 1000
vip.calculateFinalPrice(1000);     // 950
```

### 4️⃣ Abstraction

Abstract classes define general behaviors shared across subclasses.

```java
public abstract class User {
    protected String username;
    public abstract void performAction();
}
```

---

## 📂 Folder Structure

```
BookshopDiscountSystem/
├── README.md
├── pom.xml                     # Maven build configuration
├── .gitignore
├── data/                       # File-based data storage
│   ├── products.csv
│   ├── discounts.csv
│   ├── users.csv
│   └── customers.csv
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── bookshop/
│   │   │       ├── App.java                    # Main JavaFX application
│   │   │       ├── model/                      # OOP models
│   │   │       │   ├── Product.java
│   │   │       │   ├── Discount.java
│   │   │       │   ├── Customer.java
│   │   │       │   ├── RegularCustomer.java
│   │   │       │   ├── VIPCustomer.java
│   │   │       │   ├── User.java
│   │   │       │   ├── Manager.java
│   │   │       │   └── Worker.java
│   │   │       ├── service/                    # Core logic
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── DiscountService.java
│   │   │       │   ├── BillingService.java
│   │   │       │   └── CustomerService.java
│   │   │       ├── util/                       # Utility classes
│   │   │       │   ├── FileHandler.java
│   │   │       │   └── InputValidator.java
│   │   │       └── exceptions/                 # Custom exceptions
│   │   │           ├── InvalidProductException.java
│   │   │           └── InvalidQuantityException.java
│   └── test/
│       └── java/
│           └── bookshop/
│               └── BillingServiceTest.java     # Unit tests
└── target/                                     # Maven output (auto-generated)
```

---

## ⚙️ Technologies Used

| Technology       | Purpose                                    |
| ---------------- | ------------------------------------------ |
| **Java 11+**     | Core programming language                  |
| **JavaFX 17**    | GUI framework                              |
| **Maven**        | Build automation and dependency management |
| **JUnit 5**      | Unit testing                               |
| **CSV Files**    | Data persistence                           |
| **Git & GitHub** | Version control and collaboration          |

---

## 🧩 Installation & Setup

### Prerequisites

* Java 11 or newer
* Maven 3.6+
* IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/BookshopDiscountSystem.git
   cd BookshopDiscountSystem
   ```
2. **Build the Project**

   ```bash
   mvn clean install
   ```
3. **Run the Application**

   ```bash
   mvn javafx:run
   ```

---

## 🧪 Example Scenario

### ➤ Manager adds product and discounts

```
Product: Pen
Real Price: Rs.120
Discounts:
1 → Rs.100
5 → Rs.95
10 → Rs.80
```

### ➤ Worker calculates total for a VIP customer

```
Customer Type: VIP
Product: Pen x6
Subtotal = 6 × Rs.95 = Rs.570
VIP Discount (5%) = Rs.28.50
Total = Rs.541.50
```

---

## 📚 Learning Outcomes

By developing BDMS, students will:

* Apply OOP principles in a real-world business problem.
* Gain experience building modular software using JavaFX and Maven.
* Understand file handling and exception management.
* Demonstrate inheritance, polymorphism, abstraction, and encapsulation clearly.
* Build maintainable and reusable object-oriented code.
* Showcase software engineering practices suitable for academic assessment.

---
