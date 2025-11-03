# 🤖 AI Agent Development Guide for BDMS (Comprehensive)

This document provides a complete guide for developers using AI assistants for every task in the project. Providing the AI with the correct context and prompts is crucial for generating accurate and consistent code.

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

## 3. Prompts for All 13 Development Tasks

This section maps directly to the "Task Sequence Summary" in `PLAN.md`.

### **Task 1: Setup Maven Project (Member A)**
*   **Action:** This task is complete. The `pom.xml` is already configured.

### **Task 2 & 3: Define Core Models (Members A & B)**
*   **Goal:** Flesh out all data model classes with constructors and getters/setters.
*   **Files to Provide:** All files in `src/main/java/bookshop/model/`
*   **Prompt:**
    > "Your task is to complete all the data model classes in the `bookshop.model` package. For each class provided, add a constructor to initialize its fields and add public getters (and setters where appropriate) for all private fields. Ensure the code is clean and well-formatted."

### **Task 4 & 5: Implement Class Hierarchies (Members B & C)**
*   **Goal:** Ensure the `Customer` and `User` hierarchies are correctly implemented.
*   **Files to Provide:** `model/Customer.java`, `model/RegularCustomer.java`, `model/VIPCustomer.java`, `model/User.java`, `model/Manager.java`, `model/Worker.java`
*   **Prompt:**
    > "Review and complete the implementation for the model classes provided. Ensure that `RegularCustomer` and `VIPCustomer` correctly extend `Customer` and properly implement the `calculateFinalPrice` method. Ensure `Manager` and `Worker` correctly extend `User`. Add constructors where needed."

### **Task 6: Create Custom Exceptions (Member E)**
*   **Action:** This task is complete. The exception classes are already fully defined.

### **Task 7: Implement CSV & Discount Parsing (Member A)**
*   **Goal:** Implement the critical string parsing and serialization logic.
*   **Files to Provide:** `util/FileHandler.java`, `DATABASE.md`
*   **Prompt:**
    > "Your task is to fully implement the `FileHandler.java` utility. First, implement the `parseDiscountString` method to parse a string like `"5:95.0;10:80.0"` into a `Map<Integer, Double>`, handling all edge cases as described in `DATABASE.md`. Second, implement the `serializeDiscountMap` method to convert a map back into a sorted, delimited string. The code must be robust."

### **Task 8: Implement Data Services (Members B & C)**
*   **Goal:** Implement the business logic for all services.
*   **Sub-Task 8.1: `CustomerService`**
    *   **Files to Provide:** `service/CustomerService.java`, `model/RegularCustomer.java`, `model/VIPCustomer.java`, `data/customers.csv`
    *   **Prompt:**
        > "Implement the constructor for `CustomerService.java`. It must read `data/customers.csv`, create `RegularCustomer` or `VIPCustomer` objects for each row, and store them in a private list. Also, implement a `findCustomerById` method."
*   **Sub-Task 8.2: `ProductService`**
    *   **Files to Provide:** `service/ProductService.java`, `model/Product.java`, `util/FileHandler.java`, `data/products.csv`
    *   **Prompt:**
        > "Implement the constructor for `ProductService.java`. It must read `data/products.csv` and use `FileHandler.parseDiscountString` to create fully populated `Product` objects, storing them in a list. Also, implement `findProductByName` and `findProductById` methods."
*   **Sub-Task 8.3: `DiscountService`**
    *   **Files to Provide:** `service/DiscountService.java`, `util/FileHandler.java`, `DATABASE.md`
    *   **Prompt:**
        > "Implement the `addDiscount` method in `DiscountService.java`. This is a complex task that requires a full read-modify-overwrite cycle of `products.csv`, as described in `DATABASE.md`. Use the methods from `FileHandler` to complete this robustly."

### **Task 9: Implement Input Validator (Member E)**
*   **Goal:** Create helper methods for validating user input.
*   **Files to Provide:** `util/InputValidator.java`
*   **Prompt:**
    > "Implement static methods in `InputValidator.java`. Create a method `isQuantityValid(String input)` that returns true only if the input string is a positive integer. Also add a method `isStringNonEmpty(String input)`."

### **Task 10: Configure Java Module (Member D)**
*   **Action:** This task is complete. The `module-info.java` file is correctly configured.

### **Task 11: Implement Core Billing Logic (Member D)**
*   **Goal:** Implement the main calculation logic.
*   **Files to Provide:** `service/BillingService.java`, `service/ProductService.java`, `service/CustomerService.java`
*   **Prompt:**
    > "Implement the `calculateTotal` method in `BillingService.java`. It accepts a product name, quantity, and customer ID. It must use the provided services to find the product, determine the correct unit price from its discount map, calculate the sub-total, and then apply the customer-specific discount to get the final total."

### **Task 12: Develop UI & Integrate Services (Member D)**
*   **Goal:** Build the JavaFX user interface.
*   **Files to Provide:** `App.java`, `service/BillingService.java`, `PLAN.md`
*   **Prompt:**
    > "Build the user interface in `App.java` using JavaFX. Create a simple layout with:
    > 1. A `TextField` for product name.
    > 2. A `TextField` for quantity.
    > 3. A `ComboBox` for customer type (Regular, VIP).
    > 4. A `Button` to 'Calculate Total'.
    > 5. A `Label` to display the final price.
    > When the button is clicked, it should use the `BillingService` to perform the calculation and update the display label. You will need to instantiate all necessary services."

### **Task 13: Write Unit Tests (Member E)**
*   **Goal:** Create JUnit 5 tests for the business logic.
*   **Files to Provide:** `service/BillingService.java`, `service/ProductService.java`, `service/CustomerService.java`
*   **Prompt:**
    > "Write a complete JUnit 5 test suite for `BillingService.java`. Create a new file `BillingServiceTest.java`. You must use mocking for `ProductService` and `CustomerService` to isolate the billing logic. Test scenarios should include: regular customer with no discount, regular customer with a discount, VIP customer with no discount, and a VIP customer with a discount."