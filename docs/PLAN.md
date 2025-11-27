# 📘 Bookshop Discount Management System - Development Plan

This document outlines the development plan and task distribution for the project. Each of the five team members (A, B, C, D, E) has a dedicated section with a clear set of responsibilities.

**Note on Data Storage:** The data storage has been consolidated into a single `products.csv` file. This simplifies file reads but adds significant complexity to the application logic, especially for parsing and updating discount rules. The plan below has been updated to reflect these new requirements.

---

## 📋 Task Sequence Summary

This table provides a high-level overview of the development tasks in their logical sequence.

| Sequence | Task | File(s) | Assigned Member |
| :--- | :--- | :--- | :--- |
| 1 | Setup Maven Project | `pom.xml` | **A** |
| 2 | Define Core Abstract Models | `User.java`, `Customer.java` | **A** & **B** |
| 3 | Define Core Data Models | `Product.java`, `Discount.java` | **A** |
| 4 | Implement Customer Hierarchy | `RegularCustomer.java`, `VIPCustomer.java` | **B** |
| 5 | Implement User Roles | `Manager.java`, `Worker.java` | **C** |
| 6 | Create Custom Exceptions | `InvalidProductException.java`, `InvalidQuantityException.java` | **E** |
| 7 | Implement CSV & Discount Parsing | `FileHandler.java` | **A** |
| 8 | Implement Data Services | `ProductService.java`, `CustomerService.java`, `DiscountService.java` | **B** & **C** |
| 9 | Implement Input Validator | `InputValidator.java` | **E** |
| 10 | Configure Java Module | `module-info.java` | **D** |
| 11 | Implement Core Billing Logic | `BillingService.java` | **D** |
| 12 | Develop UI & Integrate Services | `App.java` | **D** |
| 13 | Write Unit Tests | `BillingServiceTest.java` | **E** |

---

## 👤 Team Member A: Chamath-Adithya (Core Models & Project Foundation)

**Overall Task**: Build the foundational blocks of the application, including Maven configuration, core models, and utilities for file I/O and discount string parsing.

| File | Task Details |
| :--- | :--- |
| `pom.xml` | - (No change) Configure project dependencies and build settings. |
| `src/.../model/User.java` | - (No change) Define the abstract `User` class. |
| `src/.../model/Product.java` | - (No change) Define fields for product data, including the `Map<Integer, Double> discountRules`. |
| `src/.../model/Discount.java` | - **This class may no longer be needed** or can be used as a simple data holder. The primary representation of discounts is now a string in `products.csv`. |
| `src/.../util/FileHandler.java` | - `readCsv` method remains.<br>- **New Task:** Implement `public static Map<Integer, Double> parseDiscountString(String discounts)` to parse a string like `"5:95.0;10:80.0"` into a map.<br>- **New Task:** Implement `public static String serializeDiscountMap(Map<Integer, Double> discountRules)` to convert a map back into a delimited string for saving. |

---

## 👤 Team Member B: yasaruhashen (Customer Hierarchy & Service)

**Overall Task**: Implement the customer logic. The `CustomerService` implementation remains largely unchanged.

| File | Task Details |
| :--- | :--- |
| `src/.../model/Customer.java` | - (No change) Define the abstract `Customer` class. |
| `src/.../model/RegularCustomer.java` | - (No change) Implement the `RegularCustomer` class. |
| `src/.../model/VIPCustomer.java` | - (No change) Implement the `VIPCustomer` class. |
| `src/.../service/CustomerService.java` | - (No change) Implement service to load customer data from `customers.csv`. |

---

## 👤 Team Member C: prabandisathsarani080-ai (User Roles & Data Management Services)

**Overall Task**: Define user roles and create services to manage product data, now with more complex logic.

| File | Task Details |
| :--- | :--- |
| `src/.../model/Manager.java` | - (No change) Implement the `Manager` class. |
| `src/.../model/Worker.java` | - (No change) Implement the `Worker` class. |
| `src/.../service/ProductService.java` | - **Major Change:** The constructor must now:<br>  1. Read `products.csv`.<br>  2. For each row, call `FileHandler.parseDiscountString()` on the `discounts` column.<br>  3. Use the resulting map to populate the `discountRules` for each `Product` object. |
| `src/.../service/DiscountService.java` | - **Major Change & High Complexity:** This service is now much harder to implement.<br>- `addDiscount` must now:<br>  1. Read the entire `products.csv` file line by line.<br>  2. Find the correct product line.<br>  3. Parse its discount string into a map.<br>  4. Add the new rule to the map.<br>  5. Serialize the map back into a string.<br>  6. Create a new line with the updated discount string.<br>  7. Write all the lines (with the one modified line) back to `products.csv`, overwriting the file. |

---

## 👤 Team Member D: IOICybErIOI (Main Application & Billing Logic)

**Overall Task**: Develop the main application entry point and UI. The core logic remains the same as it depends on the services, not the data source itself.

| File | Task Details |
| :--- | :--- |
| `module-info.java` | - (No change) Configure module dependencies. |
| `src/.../bookshop/App.java` | - (No change) Implement the JavaFX UI and integrate with the services. |
| `src/.../service/BillingService.java` | - (No change) The internal logic remains the same, as it relies on the abstractions provided by the other services. |

---

## 👤 Team Member E: kaveesha-tharindi5 (Validation, Exceptions & Testing)

**Overall Task**: Ensure application robustness. Test cases for `BillingService` remain valid.

| File | Task Details |
| :--- | :--- |
| `src/.../util/InputValidator.java` | - (No change) Implement input validation logic. |
| `src/.../exceptions/InvalidProductException.java` | - (No change) Define custom exception. |
| `src/.../exceptions/InvalidQuantityException.java` | - (No change) Define custom exception. |
| `src/test/.../BillingServiceTest.java` | - (No change) The existing test plan for the billing service is still valid as it tests the service's logic, not the data source. New tests may be needed for the parsing logic in `FileHandler`. |

---

## 🗂️ Data Files (`/data/`)

-   **`products.csv`**: `product_id,product_name,real_price,discounts` (Discounts are in the format `quantity:price;...`)
-   **`users.csv`**: `user_id,username,password,role`
-   **`customers.csv`**: `customer_id,customer_name,customer_type`
