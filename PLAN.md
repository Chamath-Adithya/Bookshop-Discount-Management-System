# 📘 Bookshop Discount Management System - Development Plan

This document outlines the development plan and task distribution for the project. Each of the five team members (A, B, C, D, E) has a dedicated section with a clear set of responsibilities.

---

##  workflow Suggestion

1.  **Foundation (Team A)**: Member A should begin first by creating the `pom.xml` and the core abstract classes and data models. Once the `pom.xml` is pushed, all other members can set up their environment.
2.  **Parallel Development (Teams B, C, E)**: Once the core models (`User`, `Product`) are defined, members B, C, and E can work in parallel.
    *   **B** can build the `Customer` hierarchy.
    *   **C** can build the `Manager`/`Worker` roles.
    *   **E** can create the custom exception classes.
3.  **Service Layer (Teams A, B, C)**: After completing the models, work on the service layer can begin. Member A's `FileHandler` is critical for the other services.
4LAGANAP.md
4.  **Integration & UI (Team D)**: Member D can initially build a mock UI. Once the services are available, they can be integrated to create a functional application.
5.  **Testing (Team E)**: Member E can write unit tests for the `BillingService` as soon as its structure is defined, using mock data initially and refining as the dependent services are completed.

---

## 👤 Team Member A: Core Models & Project Foundation

**Overall Task**: Build the foundational blocks of the application, including the Maven configuration, core data models, and the essential file I/O utility.

| File | Task Details |
| :--- | :--- |
| `pom.xml` | - Set the project's `groupId`, `artifactId`, and `version`.<br>- Configure Java version 11 properties.<br>- Add dependencies for **JavaFX 17** (`javafx-controls`, `javafx-fxml`).<br>- Add the dependency for **JUnit 5** (`junit-jupiter-api`, `junit-jupiter-engine`).<br>- Configure the `maven-compiler-plugin` and `javafx-maven-plugin`. |
| `src/.../model/User.java` | - Define as an `abstract class`.<br>- Add `protected` fields: `String username`, `String password`, `String role`.<br>- Create a constructor to initialize the fields.<br>- Add public getters for the fields. |
| `src/.../model/Product.java` | - Add `private` fields: `String productId`, `String name`, `double realPrice`, `Map<Integer, Double> discountRules`.<br>- Implement a constructor and all necessary getters/setters (Encapsulation).<br>- Create a method `public double getDiscountedPrice(int quantity)` that checks the `discountRules` map and returns the correct price for a given quantity. It should return the `realPrice` if no specific discount rule matches. |
| `src/.../model/Discount.java` | - This is a data class to represent a single discount entry.<br>- Add `private` fields: `String productId`, `int quantity`, `double price`.<br>- Create a constructor and public getters for all fields. |
| `src/.../util/FileHandler.java` | - Create a final class with a private constructor.<br>- Implement `public static List<String[]> readCsv(String filePath)` to read data from a CSV file. This method should handle `IOException`.<br>- Implement `public static void writeCsv(String filePath, List<String[]> data)` to write data to a CSV file. |

---

## 👤 Team Member B: Customer Hierarchy & Service

**Overall Task**: Implement the customer logic, demonstrating **Inheritance** and **Polymorphism** through different customer types.

| File | Task Details |
| :--- | :--- |
| `src/.../model/Customer.java` | - Define as an `abstract class`.<br>- Add `protected` fields: `String customerId`, `String name`, `String type`.<br>- Create a constructor and public getters.<br>- Define an `public abstract double calculateFinalPrice(double totalAmount);` for polymorphic behavior. |
| `src/.../model/RegularCustomer.java` | - Make this class `extend Customer`.<br>- Implement the `calculateFinalPrice` method to simply return the `totalAmount` without any change. |
| `src/.../model/VIPCustomer.java` | - Make this class `extend Customer`.<br>- Add a `private static final double VIP_DISCOUNT_RATE = 0.05;` (5% discount).<br>- Implement the `calculateFinalPrice` method to calculate and subtract the VIP discount from the `totalAmount`. |
| `src/.../service/CustomerService.java` | - Add a `private List<Customer> customers;` field.<br>- In the constructor, use `FileHandler` to read `data/customers.csv`.<br>- Populate the `customers` list by creating `RegularCustomer` or `VIPCustomer` objects based on the "type" column from the CSV.<br>- Create a `public Customer findCustomerById(String customerId)` method. |

---

## 👤 Team Member C: User Roles & Data Management Services

**Overall Task**: Define the specific application user roles and create the services responsible for managing product and discount data.

| File | Task Details |
| :--- | :--- |
| `src/.../model/Manager.java` | - Make this class `extend User`.<br>- Implement a constructor that calls `super()` with the role "MANAGER". |
| `src/.../model/Worker.java` | - Make this class `extend User`.<br>- Implement a constructor that calls `super()` with the role "WORKER". |
| `src/.../service/ProductService.java` | - Add a `private List<Product> products;` field.<br>- In the constructor, use `FileHandler` to read `data/products.csv` and `data/discounts.csv`.<br>- Populate the `products` list. For each product, populate its internal `discountRules` map from the discount data.<br>- Create `public Product findProductByName(String name)` and `public Product findProductById(String id)` methods. |
| `src/.../service/DiscountService.java` | - This service will handle modifications to discounts.<br>- Implement `public void addDiscount(String productId, int quantity, double price)` which should use `FileHandler` to append a new rule to `discounts.csv`.<br>- This service can be used by the `Manager` role in the UI. |

---

## 👤 Team Member D: Main Application & Billing Logic

**Overall Task**: Develop the main application entry point, configure the Java module, and implement the central billing logic that connects all services.

| File | Task Details |
| :--- | :--- |
| `module-info.java` | - Add `requires javafx.controls;` and `requires javafx.fxml;`.<br>- Add `opens bookshop.model to javafx.base;`.<br>- Add `exports bookshop;` to export the main package. |
| `src/.../bookshop/App.java` | - Make the class `extend javafx.application.Application`.<br>- Implement the `start(Stage primaryStage)` method.<br>- Instantiate `BillingService`, `ProductService`, and `CustomerService`.<br>- Create a basic UI with input fields (product name, quantity), a `ComboBox` for customer type, a `Button` to trigger calculation, and a `Label` to display the result. |
| `src/.../service/BillingService.java` | - Add `private ProductService productService;` and `private CustomerService customerService;` fields.<br>- Create a constructor to initialize the services.<br>- Implement `public double calculateTotal(String productName, int quantity, String customerId)`:<br>  1. Use `productService` to find the `Product`.<br>  2. Get the unit price using `product.getDiscountedPrice(quantity)`.<br>3. Calculate the sub-total.<br>  4. Use `customerService` to find the `Customer`.<br>  5. Return the final price by calling `customer.calculateFinalPrice(subTotal)`. |

---

## 👤 Team Member E: Validation, Exceptions & Testing

**Overall Task**: Ensure the application is robust and reliable by creating validation utilities, custom exceptions, and comprehensive unit tests.

| File | Task Details |
| :--- | :--- |
| `src/.../util/InputValidator.java` | - Create a final class with a private constructor.<br>- Implement `public static boolean isQuantityValid(String input)` to check if the input is a positive integer.<br>- Add other static validation methods as needed (e.g., checking for non-empty strings). |
| `src/.../exceptions/InvalidProductException.java` | - Make this class `extend Exception`.<br>- Create a constructor that takes a `String message`. This will be thrown by `BillingService` if a product cannot be found. |
| `src/.../exceptions/InvalidQuantityException.java` | - Make this class `extend Exception`.<br>- Create a constructor that takes a `String message`. This will be thrown if the quantity is not a valid positive number. |
| `src/test/.../BillingServiceTest.java` | - Use JUnit 5 (`@Test`, `@BeforeEach`).<br>- Mock the `ProductService` and `CustomerService` dependencies.<br>- Write tests for various scenarios:<br>  - Regular customer, no discount.<br>  - Regular customer, with quantity discount.<br>  - VIP customer, no quantity discount.<br>  - VIP customer, with quantity discount.<br>  - Invalid product name (should throw `InvalidProductException`). |

---

## 🗂️ Data Files (`/data/`)

These files will store the application's data. They should be created with the headers below.

-   **`products.csv`**: `product_id,product_name,real_price`
-   **`discounts.csv`**: `discount_id,product_id,quantity,discounted_price`
-   **`users.csv`**: `user_id,username,password,role` (e.g., MANAGER, WORKER)
-   **`customers.csv`**: `customer_id,customer_name,customer_type` (e.g., REGULAR, VIP)
