# Development Log - Session Summary
**Date:** November 28, 2025

This document summarizes the changes and improvements made to the Bookshop Discount Management System during this development session.

## 1. Cashier UI Redesign
**Goal:** Improve user experience and visual appeal ("Luxe Minimalist" theme).
-   **New Layout**: Switched to a 2-column design.
    -   **Left**: Product Grid with "Sale" badges and direct "Add to Cart" controls.
    -   **Right**: Customer Lookup and Shopping Cart.
-   **Styling**: Applied modern CSS for a cleaner, premium look.
-   **Cart**: Enhanced the cart list to show detailed pricing, discounts, and totals.

## 2. Functional Enhancements

### A. Stock Management
-   **Issue**: Stock was not decreasing after purchase.
-   **Fix**: Implemented logic in `handleCheckout` to:
    1.  Deduct purchased quantities from the `Product` object.
    2.  Save the updated stock levels back to `data/products.csv` immediately.
    3.  Prevent checkout if stock is insufficient.

### B. Customer Lookup
-   **Enhancement**: Enabled searching by **Name** or **Phone Number**.
-   **Autocomplete**: Replaced the "Find" button with a smart search field.
    -   As you type, a dropdown list appears with matching customers.
    -   Clicking a suggestion automatically selects the customer and applies their discount tier.

### C. Product Search
-   **Enhancement**: Implemented **Auto-Search**.
    -   Removed the manual "Search" button.
    -   The product grid now filters instantly as you type in the search bar.

## 3. Bug Fixes

### VIP Discount Calculation
-   **Issue**: The 5% VIP discount was not being applied correctly due to string comparison issues.
-   **Fix**: Refactored `CartItem.getTotalDiscount()` to use the `Customer` object's internal `baseDiscountRate` property. This ensures the discount is robust and consistent.

## 4. Security Analysis
A security audit was performed to identify potential vulnerabilities.
-   **Report**: See [Security_Audit.md](Security_Audit.md) for details.
-   **Key Findings**:
    -   Passwords stored in plain text (CSV).
    -   Hardcoded fallback credentials.
    -   Insecure data storage.
-   **Recommendations**: Implement password hashing (BCrypt), remove hardcoded accounts, and restrict file permissions.

---
