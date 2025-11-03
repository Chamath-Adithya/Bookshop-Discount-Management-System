# 🤖 AI Agent Development Guide for BDMS (Final Version)

This document provides a comprehensive guide for developers using AI assistants (like Gemini, ChatGPT, Copilot) to write code for the BDMS project. It is organized by team member to align with the project plan.

---

## 1. Introduction for the AI Agent

**Your Goal:** You are to act as an expert Java pair programmer. Your primary goal is to help a human developer implement features for the BDMS project. You must adhere to the existing project structure, conventions, and plans.

**Core Directives:**

1.  **Analyze Context First:** Always review all provided files (`PLAN.md`, `DATABASE.md`, and source code) before generating code.
2.  **Follow the Plan:** The logic you implement must align with the descriptions in the provided planning documents.
3.  **Maintain Code Style:** Match the existing code style, naming conventions, and architectural patterns.
4.  **Package Declarations:** Every `.java` file you create or modify must begin with the correct `package` declaration.

---

## 2. Project Architecture Overview

-   **Technology Stack:** Java 11, JavaFX 17, Maven, JUnit 5.
-   **Data Storage:** Single CSV file (`data/products.csv`) with a denormalized `discounts` column. Parsing this string is a key, complex task. See `DATABASE.md`.
-   **Core Layers:** `model`, `service`, `util`, `exceptions`, and the main `App.java`.

---

## 3. Task-Specific Prompts by Team Member

### **👤 Team Member A: Core Models & Foundation**

#### **Task: Complete Core Data Models**
-   **Goal:** Flesh out the `Product`, `User`, and `Discount` model classes.
-   **Files to Provide:** `model/Product.java`, `model/User.java`, `model/Discount.java`
-   **Prompt:**
    > "Your task is to complete the data model classes provided. For each class, add a constructor to initialize its fields and add public getters (and setters where appropriate) for all private fields. Ensure the code is clean and well-formatted."

#### **Task: Implement CSV & Discount Parsing Utility**
-   **Goal:** Implement the critical string parsing and serialization logic in `FileHandler`.
-   **Files to Provide:** `util/FileHandler.java`, `DATABASE.md`
-   **Prompt:**
    > "Your task is to fully implement the `FileHandler.java` utility. First, implement the `parseDiscountString` method to parse a string like `"5:95.0;10:80.0"` into a `Map<Integer, Double>`, handling all edge cases as described in `DATABASE.md`. Second, implement the `serializeDiscountMap` method to convert a map back into a sorted, delimited string."

### **👤 Team Member B: Customer Hierarchy & Service**

#### **Task: Complete Customer Model Hierarchy**
-   **Goal:** Flesh out the `Customer` abstract class and its concrete implementations.
-   **Files to Provide:** `model/Customer.java`, `model/RegularCustomer.java`, `model/VIPCustomer.java`
-   **Prompt:**
    > "Complete the `Customer` class hierarchy. Ensure the `Customer` abstract class has a constructor and getters. Verify that `RegularCustomer` and `VIPCustomer` correctly extend `Customer` and provide a correct implementation for the `calculateFinalPrice` method based on the project requirements (VIPs get a 5% discount)."

#### **Task: Implement CustomerService**
-   **Goal:** Implement the service for loading and retrieving customer data.
-   **Files to Provide:** `service/CustomerService.java`, `model/RegularCustomer.java`, `model/VIPCustomer.java`, `data/customers.csv`
-   **Prompt:**
    > "Implement the `CustomerService.java` class. The constructor must read `data/customers.csv`, create `RegularCustomer` or `VIPCustomer` objects for each row, and store them in a private list. Then, implement a `public Customer findCustomerById(String id)` method to retrieve a customer from that list."

### **👤 Team Member C: User Roles & Data Services**

#### **Task: Complete User Role Models**
-   **Goal:** Finalize the `Manager` and `Worker` classes.
-   **Files to Provide:** `model/User.java`, `model/Manager.java`, `model/Worker.java`
-   **Prompt:**
    > "Finalize the `Manager` and `Worker` classes. Ensure they extend `User` and have appropriate constructors that call the parent constructor."

#### **Task: Implement ProductService**
-   **Goal:** Implement the service for loading and retrieving product data.
-   **Files to Provide:** `service/ProductService.java`, `model/Product.java`, `util/FileHandler.java`, `data/products.csv`
-   **Prompt:**
    > "Implement the `ProductService.java` class. The constructor must read `data/products.csv` and use `FileHandler.parseDiscountString` to create fully populated `Product` objects, storing them in a list. Then, implement `public Product findProductByName(String name)` and `public Product findProductById(String id)` methods."

#### **Task: Implement DiscountService**
-   **Goal:** Implement the complex logic for updating discounts.
-   **Files to Provide:** `service/DiscountService.java`, `util/FileHandler.java`, `DATABASE.md`
-   **Prompt:**
    > "Implement the `addDiscount` method in `DiscountService.java`. This is a complex task that requires a full read-modify-overwrite cycle of `products.csv`, as described in `DATABASE.md`. Use the parsing and serialization methods from `FileHandler` to robustly handle the update."

### **👤 Team Member D: Application & Billing Logic**

#### **Task: Implement Core Billing Logic**
-   **Goal:** Implement the main calculation logic in `BillingService`.
-   **Files to Provide:** `service/BillingService.java`, `service/ProductService.java`, `service/CustomerService.java`
-   **Prompt:**
    > "Implement the `calculateTotal` method in `BillingService.java`. It accepts a product name, quantity, and customer ID. It must use the provided services to find the product, determine the correct unit price from its discount map, calculate the sub-total, and then apply the customer-specific discount to get the final total."

#### **Task: Develop UI & Integrate Services**
-   **Goal:** Build the main JavaFX user interface in `App.java`.
-   **Files to Provide:** `App.java`, `service/BillingService.java`, `PLAN.md`
-   **Prompt:**
    > "Build the user interface in `App.java` using JavaFX. Create a layout with a `TextField` for product name, a `TextField` for quantity, a `ComboBox` for customer type, a `Button` to 'Calculate Total', and a `Label` to display the price. When the button is clicked, it should use the `BillingService` to perform the calculation and update the display label. You will need to instantiate all necessary services."

### **👤 Team Member E: Validation & Testing**

#### **Task: Implement Input Validator**
-   **Goal:** Create helper methods for validating user input.
-   **Files to Provide:** `util/InputValidator.java`
-   **Prompt:**
    > "Implement static methods in `InputValidator.java`. Create a method `public static boolean isQuantityValid(String input)` that returns true only if the input string is a positive integer. Also add a method `public static boolean isStringNonEmpty(String input)`."

#### **Task: Write Unit Tests**
-   **Goal:** Create JUnit 5 tests for the business logic.
-   **Sub-Task: `BillingService` Test**
    *   **Files to Provide:** `service/BillingService.java`, `service/ProductService.java`, `service/CustomerService.java`
    *   **Prompt:**
        > "Write a complete JUnit 5 test suite for `BillingService.java` in a new `BillingServiceTest.java` file. You must use mocking for `ProductService` and `CustomerService`. Test scenarios should include: regular customer with/without discount, and VIP customer with/without discount."
-   **Sub-Task: `FileHandler` Test**
    *   **Files to Provide:** `util/FileHandler.java`
    *   **Prompt:**
        > "Write a complete JUnit 5 test suite for `FileHandler.java` in a new `FileHandlerTest.java` file. Write tests for `parseDiscountString` and `serializeDiscountMap`, covering valid inputs, empty inputs, malformed inputs, and other edge cases."
