# 🧪 UI Testing Guide - Quick Reference

## Build & Run

```bash
# Navigate to project directory
cd "d:\Java Pro\Bookshop-Discount-Management-System"

# Build the project
mvn clean compile

# Run the application
mvn javafx:run
```

**Expected Result:** Application launches with RoleSelection.fxml screen showing two buttons: "Admin Login" and "Cashier Login"

---

## Test Case 1: Admin Login & Dashboard

### Test 1.1: Admin Login Success
```
1. Click "Admin Login" button
2. Enter: username = "admin", password = "admin123"
3. Click "Login"

✅ Expected Result:
   - Admin Dashboard loads (1200×700 window)
   - Green header bar visible with "ADMIN DASHBOARD" title
   - Logout button visible in top-right
   - Left sidebar with 5 menu buttons visible
   - Products tab active showing form and table
   
📋 Verify:
   - No blank screen
   - All UI components visible
   - No errors in console
```

### Test 1.2: Admin Navigation - Menu Buttons
```
1. After successful admin login
2. Click each menu button and verify tab switches:
   
   a) Click [Products] → Products tab active
   b) Click [Discounts] → Discounts tab active
   c) Click [Customers] → Customers tab active
   d) Click [Users] → Users tab active
   e) Click [Reports] → Reports tab active
   
✅ Expected Result:
   - Tab switches correctly without errors
   - Corresponding content displays
```

### Test 1.3: Admin Reports
```
1. After successful admin login
2. Go to Reports tab
3. Click [Sales Report] button

✅ Expected Result:
   - Report text appears in TextArea:
     "=== SALES REPORT ===
      Total Sales: Rs. 50,000
      Total Transactions: 250
      Average Transaction: Rs. 200
      Top Product: Pen (500 units sold)
      Report Period: November 2025"

4. Click [Inventory Report] button
✅ Expected Result:
   - Different report appears showing inventory data

5. Click [Customers Report] button
✅ Expected Result:
   - Different report appears showing customer data
```

### Test 1.4: Admin Logout
```
1. After successful admin login
2. Click [Logout] button in top-right

✅ Expected Result:
   - Returns to RoleSelection.fxml
   - "Select Role" screen displays
   - Buttons visible: "Admin Login" and "Cashier Login"
   - No errors in console
```

### Test 1.5: Admin Login Failure
```
1. Click "Admin Login" button
2. Enter: username = "wronguser", password = "wrongpass"
3. Click "Login"

✅ Expected Result:
   - Admin Dashboard does NOT load
   - Error message appears: "Invalid admin credentials. Please try again."
   - User stays on AdminLogin.fxml
   - Can retry login or click [Back]
```

### Test 1.6: Admin Input Validation
```
1. Click "Admin Login" button
2. Leave username and password fields empty
3. Click "Login"

✅ Expected Result:
   - Error message: "Please enter username and password."
   - Login does not proceed
   - User can enter credentials and retry
```

---

## Test Case 2: Cashier Login & POS

### Test 2.1: Cashier Login Success
```
1. Click "Cashier Login" button
2. Enter: username = "cashier", password = "cashier123"
3. Click "Login"

✅ Expected Result:
   - POS Interface loads (900×600 window)
   - Three panes visible:
     a) LEFT: Time display, greeting, logout button
     b) CENTER: Item grid with pencil/pen and "Add to Cart" buttons
     c) RIGHT: Shopping cart section
   - All UI elements visible and properly laid out
   - No blank/missing sections
```

### Test 2.2: Add to Cart
```
1. After successful cashier login
2. Click "Add to Cart" button on any item (e.g., Pencil)

✅ Expected Result:
   - Item appears in cart (right pane)
   - Item shows: name, price, quantity, +/- buttons
   - Total amount updates at bottom
   - Console shows: "[UserController] Product added to cart: Pencil"
```

### Test 2.3: Add Same Item Again
```
1. After adding item to cart (Test 2.2)
2. Click "Add to Cart" again on same item

✅ Expected Result:
   - Item quantity increases (e.g., qty 1 → qty 2)
   - Total amount updates accordingly
   - Console shows: "[UserController] Updated quantity for Pencil: 2"
```

### Test 2.4: Clear Cart
```
1. After adding items to cart
2. Click "Clear" button (red button in cart section)

✅ Expected Result:
   - All items removed from cart
   - Cart section empty
   - Total amount resets to Rs. 0.00
   - Message appears (optional): "Cart cleared"
```

### Test 2.5: Payment - Show Receipt
```
1. After adding items to cart (at least 1 item):
   - Item: Pencil, Qty: 1, Price: Rs.50
   - Item: Pen, Qty: 2, Price: Rs.95 each = Rs.190
   - Total: Rs.240

2. Click "Pay" button

✅ Expected Result:
   - Receipt dialog appears showing:
     "========== RECEIPT ==========
      Date: 20/11/2025 15:30 (current date/time)
      ----------
      Pencil x1 @ Rs. 50.00 = Rs. 50.00
      Pen x2 @ Rs. 95.00 = Rs. 190.00
      ----------
      Total: Rs. 240.00
      =============================
      Thank you for shopping!"
   
3. Click OK on receipt dialog

✅ Expected Result:
   - Cart clears automatically
   - Ready for next transaction
   - Console shows: "[UserController] Payment processed successfully"
```

### Test 2.6: Pay with Empty Cart
```
1. After successful cashier login (don't add any items)
2. Click "Pay" button

✅ Expected Result:
   - Warning dialog appears:
     "Shopping cart is empty. Please add items before paying."
   - Payment does not process
   - Cart remains empty
```

### Test 2.7: Clear Empty Cart
```
1. After successful cashier login (don't add any items)
2. Click "Clear" button

✅ Expected Result:
   - Info dialog appears:
     "Cart is already empty"
   - No error
   - Cart remains empty
```

### Test 2.8: Cashier Logout
```
1. After successful cashier login
2. Click "Logout" button (left pane)

✅ Expected Result:
   - Returns to RoleSelection.fxml
   - "Select Role" screen displays
   - Buttons visible: "Admin Login" and "Cashier Login"
   - No errors in console
```

### Test 2.9: Cashier Login Failure
```
1. Click "Cashier Login" button
2. Enter: username = "wronguser", password = "wrongpass"
3. Click "Login"

✅ Expected Result:
   - POS does NOT load
   - Error message appears: "Invalid cashier credentials. Please try again."
   - User stays on CashierLogin.fxml
   - Can retry or click [Back]
```

### Test 2.10: Search Functionality (Placeholder)
```
1. After successful cashier login
2. Type in search field: "Pen"
3. Click "Search" button or press Enter

✅ Current Result:
   - Console shows: "[UserController] Searching for: Pen"
   - No filtering happens yet (TODO feature)
   - All items still visible in grid

Note: Search filtering is marked as TODO in code.
      Full implementation pending product grid integration.
```

---

## Test Case 3: Complete Navigation Flow

### Test 3.1: Full Admin Path
```
1. Start at RoleSelection.fxml
2. Click "Admin Login"
3. Enter admin credentials
4. Click around menu buttons to verify navigation
5. Click [Logout]
6. Verify back at RoleSelection.fxml

✅ All steps complete successfully
```

### Test 3.2: Full Cashier Path
```
1. Start at RoleSelection.fxml
2. Click "Cashier Login"
3. Enter cashier credentials
4. Add items to cart
5. Click [Pay] and verify receipt
6. Click [Logout]
7. Verify back at RoleSelection.fxml

✅ All steps complete successfully
```

### Test 3.3: Switch Between Admin and Cashier
```
1. Login as Admin
2. Click [Logout]
3. Click "Cashier Login"
4. Login as Cashier
5. Add items and pay
6. Click [Logout]
7. Click "Admin Login"
8. Login as Admin
9. Verify dashboard loads

✅ No data corruption or crashes during switches
```

---

## Test Case 4: Error Handling

### Test 4.1: Product Loading Error
```
(Delete or rename data/products.csv temporarily)
1. Click "Cashier Login"
2. Login with valid credentials
3. Observe what happens

✅ Expected Result:
   - Alert appears: "Failed to load product data: [error message]"
   - POS interface loads but no items in grid
   - System continues functioning (graceful degradation)

(Restore data/products.csv)
```

### Test 4.2: File Not Found Error
```
(Rename RoleSelection.fxml temporarily to break navigation)
1. Click "Admin Login"
2. Enter credentials
3. Click [Logout]

✅ Expected Result:
   - Error appears in console
   - Alert dialog shows: "Cannot go back: [error message]"
   - User stays on Admin.fxml (cannot logout)

(Restore RoleSelection.fxml)
```

### Test 4.3: Null Pointer Handling
```
All of the following should NOT crash with NullPointerException:
- Click "Pay" with empty cart
- Click "Clear" with empty cart
- Add to cart without selecting item
- Logout from any screen
- Click menu buttons rapidly
- Type in search field and press Enter with no products

✅ No unhandled exceptions or crashes
✅ Proper error messages displayed to user
```

---

## Console Output Verification

### Expected Console Logs During Admin Login

```
[AdminLoginController] Login attempt - Username: 'admin'
[AdminLoginController] Authenticating user: 'admin' for role MANAGER
[AuthService] Read 6 lines from data/users.csv
[AuthService] Comparing - Input: admin vs CSV: admin
[AuthService] Username and password match!
[AuthService] Role check: CSV role=MANAGER, required=MANAGER, match=true
[AdminLoginController] Authentication successful! Loading Admin Dashboard...
[AdminController] Initializing Admin Dashboard...
[AdminController] Loading products data...
[AdminController] Loading discounts data...
[AdminController] Loading customers data...
[AdminController] Loading users data...
```

### Expected Console Logs During Cashier Login

```
[CashierLoginController] Login attempt - Username: 'cashier'
[CashierLoginController] Authenticating user: 'cashier' for role WORKER
[AuthService] Read 6 lines from data/users.csv
[AuthService] Comparing - Input: cashier vs CSV: cashier
[AuthService] Username and password match!
[AuthService] Role check: CSV role=WORKER, required=WORKER, match=true
[CashierLoginController] Authentication successful! Loading POS...
[UserController] Initializing POS Interface...
[UserController] ProductService loaded successfully
```

### Expected Console Logs During Cart Operations

```
[UserController] Add to Cart action triggered
[UserController] Product added to cart: Pen
[UserController] Cart updated. Total items: 1, Total amount: Rs. 95.00

[UserController] Clear Cart button clicked
[UserController] Cart updated. Total items: 0, Total amount: Rs. 0.00

[UserController] Pay button clicked
[UserController] Payment processed successfully. Amount: Rs. 240.00
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| **Blank admin screen after login** | Check Admin.fxml has UI content. Run `mvn clean compile`. Verify AdminController.java is not empty. |
| **Buttons don't work** | Check FXML `onAction="#handleMethodName"` matches controller method name. Ensure method is `@FXML` annotated. |
| **"File not found" error** | Verify FXML paths are correct: `/FXML/Admin/Admin.fxml`, `/FXML/User/User.fxml`, etc. |
| **"Controller not found" error** | Check FXML `fx:controller="bookshop.controllers.Admin.AdminController"` path is correct and class exists. |
| **Shopping cart empty** | ProductService may not have loaded products. Check data/products.csv exists and has data. |
| **Pay button does nothing** | Check UserController.handlePay() is `@FXML` annotated. Check for exceptions in console. |
| **Login fails with correct credentials** | Check data/users.csv has test accounts and is properly formatted. Verify AuthService reads CSV correctly. |

---

## Valid Test Credentials

### Admin Accounts
```
Username: admin
Password: admin123
Role: MANAGER

Username: manager
Password: manager123
Role: MANAGER
```

### Cashier Accounts
```
Username: cashier
Password: cashier123
Role: WORKER

Username: john
Password: john456
Role: WORKER

Username: sarah
Password: sarah789
Role: WORKER
```

---

## Summary

✅ Build project with `mvn clean compile`
✅ Run with `mvn javafx:run`
✅ Test admin login and dashboard navigation
✅ Test cashier login and POS cart operations
✅ Verify all buttons work correctly
✅ Check error handling
✅ Verify logout returns to role selection
✅ Monitor console for expected log messages
✅ No crashes or unhandled exceptions

**All tests should pass without errors!**
