# 🤖 AI Agent Development Guide for BDMS

This document provides a guide for developers using AI assistants (like Gemini, ChatGPT, Copilot) to write code for the Bookshop Discount Management System (BDMS). Providing the AI with the correct context and prompts is crucial for generating accurate and consistent code.

---

## 1. Introduction for the AI Agent

**Your Goal:** You are to act as an expert Java pair programmer. Your primary goal is to help a human developer implement features for the BDMS project. You must adhere to the existing project structure, conventions, and plans.

**Core Directives:**

1.  **Analyze Context First:** Always review all provided files (`PLAN.md`, `DATABASE.md`, and source code) before generating code.
2.  **Follow the Plan:** The logic you implement must align with the descriptions in the provided planning documents.
3.  **Maintain Code Style:** Match the existing code style, naming conventions, and architectural patterns.
4.  **Package Declarations:** Every `.java` file you create or modify must begin with the correct `package` declaration (e.g., `package bookshop.model;`).
5.  **Focus on the Task:** Only implement the logic requested in the prompt. Do not add extra features or classes.

---

## 2. Project Architecture Overview

-   **Technology Stack:** Java 11, JavaFX 17, Maven, JUnit 5.
-   **Data Storage:** The project uses a **single, denormalized CSV file** (`data/products.csv`) for product and discount data. This is a critical constraint.
-   **Discount Format:** Discounts are stored as a single string (`"5:95.0;10:80.0"`) in the `products.csv` file. A key, complex task is parsing this string into a `Map` and serializing it back. The full details are in `DATABASE.md`.
-   **Core Layers:**
    -   `model`: Plain Old Java Objects (POJOs) that represent the application's data.
    -   `service`: Classes that contain the core business logic (e.g., calculating bills, managing products).
    -   `util`: Helper classes, especially `FileHandler` for all file I/O and discount string parsing.
    -   `App.java`: The main entry point for the JavaFX application.

---

## 3. Task-Specific Prompts for Developers

Here are example prompts to give to an AI agent for each major task. Always provide the specified files along with the prompt.

### **Team Member A: Core Utilities**

#### **Task: Implement Discount String Parsing**

-   **Files to Provide:**
    1.  `src/main/java/bookshop/util/FileHandler.java`
    2.  `DATABASE.md`
-   **Prompt:**
    > "You are an expert Java developer. Your task is to fully implement the `parseDiscountString` method in the provided `FileHandler.java` file. The logic must parse a discount string like `"5:95.0;10:80.0"` into a `Map<Integer, Double>`. Refer to the `DATABASE.md` file for the exact data format. Your implementation must be robust: handle empty strings (return an empty map), null strings, and potential number format errors inside the string gracefully (e.g., by skipping malformed pairs). Here are the files:"

#### **Task: Implement Discount Map Serialization**

-   **Files to Provide:**
    1.  `src/main/java/bookshop/util/FileHandler.java`
-   **Prompt:**
    > "Now, implement the `serializeDiscountMap` method in `FileHandler.java`. This method should perform the reverse of parsing: convert a `Map<Integer, Double>` back into a sorted, delimited string. For example, a map containing `{10=80.0, 5=95.0}` should become `"5:95.0;10:80.0"`. The keys (quantities) must be sorted numerically before building the string. Here is the file:"

### **Team Member C: Data Services**

#### **Task: Implement ProductService Constructor**

-   **Files to Provide:**
    1.  `src/main/java/bookshop/service/ProductService.java`
    2.  `src/main/java/bookshop/model/Product.java`
    3.  `src/main/java/bookshop/util/FileHandler.java`
    4.  `data/products.csv`
-   **Prompt:**
    > "Your task is to implement the constructor of the `ProductService` class. This constructor needs to read the `data/products.csv` file, and for each row, create a fully populated `Product` object. This includes calling the `FileHandler.parseDiscountString()` method to convert the discount string into the `discountRules` map for the `Product` object. Store the created `Product` objects in a private list within the service. You can write a simple, temporary CSV reader inside the constructor for now. Here are the files:"

### **Team Member D: Core Business Logic**

#### **Task: Implement BillingService Logic**

-   **Files to Provide:**
    1.  `src/main/java/bookshop/service/BillingService.java`
    2.  `src/main/java/bookshop/service/ProductService.java`
    3.  `src/main/java/bookshop/service/CustomerService.java`
    4.  `src/main/java/bookshop/model/Product.java`
    5.  `src/main/java/bookshop/model/Customer.java`
-   **Prompt:**
    > "Implement the `calculateTotal` method in `BillingService.java`. This method will accept a product name, a quantity, and a customer ID. Your logic should:
    > 1. Use the `ProductService` to find the correct `Product` object.
    > 2. From the `Product` object, determine the correct unit price by checking its internal `discountRules` map against the given quantity.
    > 3. Calculate the sub-total.
    > 4. Use the `CustomerService` to find the `Customer` object and apply their final discount (e.g., for VIPs) by calling the polymorphic `calculateFinalPrice` method.
    > 5. Return the final calculated total. Here are the relevant files:"

### **Team Member E: Testing**

#### **Task: Write Unit Tests for FileHandler**

-   **Files to Provide:**
    1.  `src/main/java/bookshop/util/FileHandler.java`
    2.  `src/test/java/bookshop/BillingServiceTest.java` (as a style guide)
-   **Prompt:**
    > "You are a Java testing expert using JUnit 5. Your task is to write a complete test suite for the `FileHandler` class. Create a new file named `FileHandlerTest.java` in the `src/test/java/bookshop/` directory. Write tests for the `parseDiscountString` method, covering:
    > - A standard, valid discount string.
    > - An empty discount string.
    > - A string with a single rule.
    > - A string with malformed pairs (e.g., `"5:95;abc;10:80"`).
    > Also, write tests for the `serializeDiscountMap` method to ensure it correctly converts a map back to a sorted string. Here are the files for context and style:"
