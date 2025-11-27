# 🧠 Object-Oriented Programming (OOP) Concepts in BDMS

This document provides a detailed technical breakdown of how the **Bookshop Discount Management System (BDMS)** utilizes the four fundamental pillars of Object-Oriented Programming (OOP). This project was designed specifically to demonstrate these concepts in a real-world application scenario.

---

## 1️⃣ Encapsulation

**Definition**: Encapsulation is the bundling of data (variables) and methods (functions) that operate on that data into a single unit (class), and restricting direct access to some of an object's components.

### Implementation in BDMS
In this project, we use **private fields** to hide the internal state of objects and **public getters/setters** to control access. This ensures that data cannot be modified in invalid ways (e.g., setting a negative price).

#### Example: `Product.java`
The `Product` class encapsulates product details. The `realPrice` field is private, and the setter ensures validation.

```java
public class Product {
    // Private fields: Data is hidden from the outside world
    private String productId;
    private String name;
    private double realPrice;
    private Map<Integer, Double> discountRules;

    // Public Constructor: Controlled initialization
    public Product(String productId, String name, double realPrice) {
        this.productId = productId;
        this.name = name;
        this.realPrice = realPrice;
        this.discountRules = new HashMap<>();
    }

    // Public Getter: Read-only access to price
    public double getRealPrice() {
        return realPrice;
    }

    // Public Setter: Controlled modification with validation
    public void setRealPrice(double realPrice) {
        if (realPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.realPrice = realPrice;
    }
}
```

**Benefit**: This prevents the system from ever having a product with a negative price, ensuring data integrity throughout the application lifecycle.

---

## 2️⃣ Inheritance

**Definition**: Inheritance is a mechanism where a new class derives properties and characteristics (methods and fields) from an existing class. It promotes code reusability and establishes a relationship between classes.

### Implementation in BDMS
We use inheritance to create specialized versions of generic concepts.

#### Example: Customer Hierarchy
We have a base `Customer` class, and two specific types of customers: `RegularCustomer` and `VIPCustomer`.

```java
// Base Class (Parent)
public abstract class Customer {
    protected String customerId;
    protected String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }
    
    // Common method shared by all subclasses
    public String getName() {
        return name;
    }
    
    // Abstract method forcing subclasses to define their own discount logic
    public abstract double getBaseDiscountRate();
}

// Derived Class (Child)
public class VIPCustomer extends Customer {
    public VIPCustomer(String customerId, String name) {
        super(customerId, name); // Calls the parent constructor
    }

    // VIPs get specific behavior
    @Override
    public double getBaseDiscountRate() {
        return 0.05; // 5% discount
    }
}
```

**Benefit**: We don't need to rewrite the `name` and `customerId` logic for every type of customer. If we want to add a `SuperVIPCustomer` later, we just extend `Customer` again.

---

## 3️⃣ Polymorphism

**Definition**: Polymorphism allows objects to be treated as instances of their parent class rather than their actual class. The specific behavior is determined at runtime (Method Overriding).

### Implementation in BDMS
The system processes all customers uniformly as `Customer` objects, but the discount calculation behaves differently depending on whether the object is actually a `RegularCustomer` or `VIPCustomer`.

#### Example: Checkout Logic
In `CashierController`, we calculate the final price without checking the specific type of customer with `if-else` statements.

```java
// We hold a reference to the abstract Parent class
Customer currentCustomer = customerService.findCustomer("John");

// POLYMORPHISM IN ACTION:
// If currentCustomer is VIP, this calls VIPCustomer.getBaseDiscountRate() (0.05)
// If currentCustomer is Regular, this calls RegularCustomer.getBaseDiscountRate() (0.0)
double discountRate = currentCustomer.getBaseDiscountRate();

double discountAmount = subtotal * discountRate;
```

**Benefit**: The code is cleaner and more flexible. The checkout logic doesn't need to change if we add new customer types.

---

## 4️⃣ Abstraction

**Definition**: Abstraction is the concept of hiding complex implementation details and showing only the essential features of the object. It reduces complexity and allows the developer to focus on interactions rather than details.

### Implementation in BDMS
We use **Abstract Classes** and **Service Layers** to hide complexity.

#### Example 1: `FileHandler` Utility
The application needs to read/write CSV files, handle file locking, parse strings, and manage IO exceptions. All this complexity is hidden behind simple static methods.

```java
// Complex implementation is hidden inside FileHandler.java
// The developer just calls:
List<String> lines = FileHandler.readCsv("data/products.csv");
```

#### Example 2: Abstract `User` Class
We define a template for what a User is, but we prevent anyone from creating just a generic "User". They must create a `Manager` or `Worker`.

```java
public abstract class User {
    protected String username;
    protected String passwordHash;
    protected String role;

    // Abstract method: Every user must have a dashboard, 
    // but Managers and Workers have different dashboards.
    public abstract String getDashboardView();
}
```

**Benefit**: This enforces a clear structure. A developer cannot accidentally create a `User` object that doesn't know which dashboard to open.

---

## 5️⃣ Other Design Patterns & Principles

### MVC (Model-View-Controller)
The project follows the MVC architectural pattern:
*   **Model**: `Product`, `Customer`, `User` (Data structures).
*   **View**: `.fxml` files (The UI layout).
*   **Controller**: `CashierController`, `AdminController` (The logic connecting UI and Data).

### Service Layer Pattern
Business logic is separated from the UI controllers.
*   `ProductService`: Handles loading products and parsing discount rules.
*   `CustomerService`: Handles searching and validating customers.
*   `AuthService`: Handles login and password hashing.

This separation makes the code **testable** and **maintainable**.
