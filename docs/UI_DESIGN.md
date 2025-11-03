# 🎨 UI Design and User Flow

## 1. Overview

This document describes the proposed user interface (UI) design and screen flow for the Bookshop Discount Management System (BDMS). The UI will be built using standard **JavaFX 17** components.

The application will feature a multi-screen interface that presents different views based on the authenticated user's role (Worker or Manager).

---

## 2. Screen Descriptions

### Screen 1: Login Screen

-   **Purpose:** To securely authenticate a user and grant access to the application based on their role.
-   **Layout:** A simple, centered form in a fixed-size window.

-   **Components:**
    -   `Label`: "Username"
    -   `TextField`: For username input.
    -   `Label`: "Password"
    -   `PasswordField`: For secure password input.
    -   `Button`: "Login"
    -   `Label`: An area for displaying error messages (e.g., "Invalid credentials, please try again").

-   **User Flow:**
    1.  The user launches the application, and the Login Screen appears.
    2.  The user enters their credentials and clicks the "Login" button.
    3.  The application attempts to authenticate the user against the data in `users.csv`.
    4.  **On Success:** The Login Screen closes. If the user is a "Worker" or "Manager", the **Main Billing Screen** opens.
    5.  **On Failure:** The error message `Label` becomes visible with an appropriate message.

### Screen 2: Main Billing Screen

-   **Purpose:** This is the primary screen for daily use, designed for quickly calculating the final price of a purchase for a customer.
-   **Layout:** A vertical layout (`VBox`) containing input controls, an action button, and a results display area. A `MenuBar` will be visible at the top for Managers.

-   **Components:**
    -   **`MenuBar` (Managers Only):** A top menu with a "File" menu containing two items: "Manage Products" and "Logout".
    -   `Label` & `TextField`: For entering the **Product Name**. (This could later be enhanced with auto-completion).
    -   `Label` & `TextField`: For entering the **Quantity**.
    -   `Label` & `ComboBox<String>`: A dropdown to select the **Customer Type** ("Regular" or "VIP").
    -   `Button`: A prominent "Calculate Total" button.
    -   `Separator`: A visual line to divide the input section from the results.
    -   `Label`: A display for the final calculated price (e.g., "**Final Price: Rs. 541.50**"). This should be large and clear.
    -   `Label`: A status bar at the bottom for messages like "Product not found" or "Invalid quantity".

-   **User Flow:**
    1.  A logged-in user sees this screen.
    2.  They enter a product name and quantity and select the customer type.
    3.  They click "Calculate Total".
    4.  The application calls the `BillingService`, which performs the calculation.
    5.  The final price `Label` is updated with the result.
    6.  If an error occurs (e.g., product not found), the status bar `Label` displays the error.
    7.  A Manager can navigate to the **Product Management Screen** via the menu.

### Screen 3: Product Management Screen (Manager Only)

-   **Purpose:** An administrative screen for managers to perform CRUD (Create, Read, Update, Delete) operations on products and their discounts.
-   **Layout:** A main window dominated by a `TableView` with control buttons below it.

-   **Components:**
    -   `TableView`: A table displaying all products from `products.csv`. Columns will include "Product ID", "Product Name", and "Base Price".
    -   `Button`: "Add New Product..."
    -   `Button`: "Edit Selected Product..."
    -   `Button`: "Delete Selected Product"

-   **User Flow & Dialogs:**
    1.  The Manager selects "Manage Products" from the Main Billing Screen's menu to open this window.
    2.  The `TableView` is immediately populated with all products.
    3.  **To Add:** The manager clicks "Add New Product". This opens a **new dialog window (the "Add/Edit Product Form")** with fields for Product Name, Base Price, and a simple interface (e.g., a small table or dynamic text fields) to add quantity-price discount rules.
    4.  **To Edit:** The manager selects a product in the table and clicks "Edit Selected Product". This opens the same **Add/Edit Product Form**, pre-populated with the data of the selected product, allowing the manager to change its name, price, or discount rules.
    5.  **To Delete:** The manager selects a product and clicks "Delete". A confirmation dialog appears. If confirmed, the product is removed.
    6.  Any changes made are saved back to `products.csv` via the appropriate service classes.
