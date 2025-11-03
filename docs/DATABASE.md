# 🗂️ Data Storage Architecture

## 1. Overview

This project does not use a traditional relational database system like MySQL or PostgreSQL. Instead, it employs a **flat-file database architecture** using **Comma-Separated Values (CSV) files** as its data store.

This approach was chosen to ensure the application is:

-   **Lightweight:** No external database software is required to run the application.
-   **Portable:** The entire application, including its data, can be easily moved and run from any location.
-   **Simple:** The data is human-readable and can be edited with a simple text editor or spreadsheet software.

All data files are located in the `/data/` directory of the project.

---

## 2. Database Structure and Schema

The "database" consists of three main tables (files), each representing a different entity in the system.

### `products.csv`

Stores the master data for all products, including their complex discount rules.

**Columns:**

| Column | Data Type | Description |
| :--- | :--- | :--- |
| `product_id` | `String` | The unique identifier for the product (e.g., `p01`). |
| `product_name` | `String` | The name of the product (e.g., "Pen"). |
| `real_price` | `Double` | The base price of the product when purchased in a quantity of 1. |
| `discounts` | `String` | A formatted string representing all quantity-based discount rules. |

**Discount String Format:**

The `discounts` column uses a special delimited format to store a dynamic number of rules:

-   Each rule is represented as `quantity:price`.
-   Multiple rules are separated by a semicolon `;`.
-   **Example:** `"5:95.0;10:80.0"` means if the quantity is 5 or more, the price is 95.0 each; if the quantity is 10 or more, the price is 80.0 each.

### `customers.csv`

Stores information about registered customers.

**Columns:**

| Column | Data Type | Description |
| :--- | :--- | :--- |
| `customer_id` | `String` | The unique identifier for the customer. |
| `customer_name` | `String` | The name of the customer. |
| `customer_type` | `String` | The type of customer, which determines their discount level (`REGULAR` or `VIP`). |

### `users.csv`

Stores credentials for application users (staff).

**Columns:**

| Column | Data Type | Description |
| :--- | :--- | :--- |
| `user_id` | `String` | The unique identifier for the user. |
| `username` | `String` | The username for logging into the application. |
| `password` | `String` | The user's password (in a real-world scenario, this should be hashed). |
| `role` | `String` | The user's role, which determines their permissions (`MANAGER` or `WORKER`). |

---

## 3. Data Read and Write Architecture

The application interacts with the CSV files through a set of service classes that manage the logic for reading and writing data.

### Data Read Process

Data is read from the CSV files when the application starts and is loaded into memory. This collection of in-memory Java objects serves as the live, running database for the user's session.

1.  **Initiation:** On startup, the `ProductService`, `CustomerService`, etc., are initialized.
2.  **File Reading:** Each service uses the `FileHandler.readCsv()` utility to read the raw lines from its corresponding CSV file.
3.  **Object Hydration (Example with Products):**
    *   The `ProductService` iterates through each row from `products.csv`.
    *   For each row, it calls `FileHandler.parseDiscountString()` to convert the `discounts` string (e.g., `"5:95.0;10:80.0"`) into a Java `Map<Integer, Double>`.
    *   A new `Product` object is created in memory, and its `discountRules` map is populated with the result.
    *   The fully formed `Product` object is added to a list within the `ProductService`.

After this process, the application works with these in-memory objects, providing fast access without further file reads.

### Data Write/Update Process

Writing or updating data is more complex due to the nature of flat files. The application must perform a **read-modify-overwrite** cycle.

This is most evident when a Manager updates a product's discount rules:

1.  **Initiation:** The `DiscountService` is called to add a new discount to a product.
2.  **Read All Data:** The service reads the *entire* `products.csv` file into a list of lines.
3.  **Locate and Modify:**
    *   It searches the list to find the line corresponding to the product being edited.
    *   It parses the `discounts` string from that line into a `Map`.
    *   It adds the new discount rule to the `Map`.
    *   It serializes the updated `Map` back into a string (e.g., `"5:95.0;10:80.0;20:75.0"`) using `FileHandler.serializeDiscountMap()`.
    *   It replaces the old line with a new line containing the updated discount string.
4.  **Overwrite File:** The service then writes the entire, modified list of lines back to `products.csv`, completely overwriting the old file with the new version.

This architecture centralizes data access logic within the service layer and provides a workable, if complex, solution for data persistence without a traditional database engine.
