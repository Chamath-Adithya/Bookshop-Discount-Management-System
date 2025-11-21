# Quick Reference: All Event Handlers

## AdminController - src/main/java/bookshop/controllers/Admin/AdminController.java

### Menu Navigation
```
handleProductsMenu()    → Selects Products tab
handleDiscountsMenu()   → Selects Discounts tab
handleCustomersMenu()   → Selects Customers tab
handleUsersMenu()       → Selects Users tab
handleReportsMenu()     → Selects Reports tab
handleLogout()          → Returns to RoleSelection
```

### Products Tab
```
handleAddProduct()         → Add or Update product (checks editingProduct flag)
Row Click (setOnMouseClicked)  → Populate form fields, set editingProduct=true
Delete Button (Cell Factory)   → Remove product from CSV
```

### Discounts Tab
```
handleAddDiscount()     → Add discount rule to product
```

### Users Tab
```
handleAddUser()         → Add or Update user (checks editingUser flag)
Row Click (setOnMouseClicked)  → Populate form fields, set editingUser=true
Delete Button (Cell Factory)   → Remove user from CSV
```

### Reports Tab
```
handleSalesReport()     → Display sales metrics
handleInventoryReport() → Display inventory metrics
handleCustomersReport() → Display customer metrics
```

---

## UserController - src/main/java/bookshop/controllers/User/UserController.java

### Navigation & UI
```
handleLogout()          → Returns to RoleSelection, shuts down file watcher
handleSearch()          → Filter products by name (TODO: implement)
handleRefreshProducts() → Reload products from CSV immediately
updateTimeAndDate()     → Update clock display (called in initialize)
```

### Shopping Cart
```
handleAddToCart(ActionEvent)   → Add item to cart (for dynamic buttons)
handleAddToCart(MouseEvent)    → Add item to cart (for static FXML items)
addToCartByNode()              → Helper: extract product and add to cart
handleClearCart()              → Empty cart
handlePay()                    → Process payment and show receipt
updateCartDisplay()            → Refresh cart UI with current items
```

### Product Loading
```
loadProductsToGrid()    → Load all products from ProductService and display as grid cards
initialize()            → Setup UI, start file watcher, load products
```

### Background Sync
```
File Watcher (3-second poll) → Detect products.csv changes
                            → Load fresh ProductService
                            → Call loadProductsToGrid() to refresh UI
```

---

## CashierLoginController - src/main/java/bookshop/controllers/CashierLoginController.java

```
handleLogin()     → Authenticate against users.csv with role=WORKER
                  → Trim inputs, require non-empty password
                  → Accept role variants (WORKER, WORKER1, etc.)
handleBack()      → Return to RoleSelection
```

---

## AdminLoginController - src/main/java/bookshop/controllers/AdminLoginController.java

```
handleLogin()     → Authenticate against users.csv with role=MANAGER
                  → Trim inputs, require non-empty password
handleBack()      → Return to RoleSelection
```

---

## AuthService - src/main/java/bookshop/service/AuthService.java

```
authenticate(username, password, requiredRole)
    ├─ Check users.csv for matching username/password
    ├─ Validate role with flexibility:
    │   ├─ Exact match: MANAGER = MANAGER ✓
    │   ├─ Prefix match: WORKER1 starts with WORKER ✓
    │   └─ Case-insensitive: worker = WORKER ✓
    ├─ Fallback demo accounts (admin/admin, user/user)
    └─ Return boolean success
```

---

## FXML Event Bindings

### Admin.fxml
```
Login        → AdminLoginController.handleLogin()
Logout       → AdminController.handleLogout()
Menu Buttons → AdminController.handleXxxMenu()
Add Buttons  → AdminController.handleAddXxx()
Rows         → Mouse click listener in loadXxxData()
Delete       → Cell factory buttons in loadXxxData()
```

### User.fxml (Cashier)
```
Logout        → UserController.handleLogout()
Search Button → UserController.handleSearch()
Refresh       → UserController.handleRefreshProducts()
Product Cards (FXML) → onMouseClicked="#handleAddToCart"
Add Buttons (dynamic) → setOnAction(this::handleAddToCart)
Cart Clear    → UserController.handleClearCart()
Cart Pay      → UserController.handlePay()
Qty ±         → Lambda handlers in updateCartDisplay()
```

### CashierLogin.fxml
```
Login  → CashierLoginController.handleLogin()
Back   → CashierLoginController.handleBack()
```

### AdminLogin.fxml
```
Login  → AdminLoginController.handleLogin()
Back   → AdminLoginController.handleBack()
```

---

## CSV Files & Persistence

### data/products.csv
```
Header: product_id,product_name,real_price,discounts,quantity
Update: ProductService.addProduct() or ProductService.saveAllProducts()
Read:   ProductService.loadProducts() (via ProductService constructor)
```

### data/users.csv
```
Header: user_id,username,password,role
Update: FileHandler.appendLine() or FileHandler.writeCsv()
Read:   FileHandler.readCsv()
Auth:   AuthService.authenticate()
```

### data/customers.csv
```
Header: customer_id,name,type,phone
Update: CustomerService (read-only in current impl)
Read:   CustomerService.getAllCustomers()
```

---

## Data Flow Diagrams

### Admin Update → Cashier Refresh
```
Admin: Click row or Add button
       ↓
Admin: handleAddProduct() or handleAddUser()
       ↓
       ProductService.saveAllProducts() or FileHandler.writeCsv()
       ↓
       Write to data/products.csv or data/users.csv
       ↓
       [File system]
       ↓
Cashier: File Watcher detects change (every 3 seconds)
         ↓
         Create new ProductService (loads fresh CSV)
         ↓
         Call loadProductsToGrid()
         ↓
         UI updates with new products (automatic or manual Refresh)
```

### Cashier Add to Cart Flow
```
Cashier: Click "Add to Cart" button on product
         ↓
         FXML triggers handleAddToCart (ActionEvent or MouseEvent)
         ↓
         addToCartByNode(source)
         ↓
         Find product card VBox from node hierarchy
         ↓
         Extract product ID from userData
         ↓
         ProductService.findProductById(id)
         ↓
         addProductToCart(product, qty)
         ↓
         updateCartDisplay()
         ↓
         Show success alert
```

---

## Event Handler Compatibility Matrix

| Event Type | FXML Attribute | Java Handler Type | Status |
|-----------|---|---|---|
| Button Click | onAction="#handler" | ActionEvent | ✅ Both |
| Mouse Click | onMouseClicked="#handler" | MouseEvent | ✅ Both |
| Text Input | onAction="#handler" | ActionEvent | ✅ Both |
| Row Selection | Listener registered in code | Lambda | ✅ Custom |
| Cell Factory | setCellFactory(col → ...) | None (UI rendering) | ✅ Custom |

---

## Common Issues & Solutions

### Issue: Cashier login fails
**Solution:** Check AuthService role matching - now accepts WORKER, WORKER1, WORKER_A, etc.

### Issue: Admin updates not visible in Cashier
**Solution:** 
- Automatic: Wait 3-6 seconds (file watcher polls every 3 seconds)
- Manual: Click Refresh button in Cashier

### Issue: Product not found when adding to cart
**Solution:** 
- Ensure product ID is stored in productCard.userData
- Check ProductService.findProductById() returns non-null

### Issue: Delete button doesn't work
**Solution:** 
- Delete uses cell factory (new feature)
- Ensure FileHandler.writeCsv() can write to file
- Check CSV file is not locked by another process

---

## Performance Notes

- **File Watcher Interval:** 3 seconds (configurable in UserController.initialize())
- **ProductService Load Time:** ~100ms for 50 products (depends on CSV size)
- **Grid Rendering:** Dynamic generation of 2-column grid (updates on every refresh)
- **Memory:** Singleton ProductService per UserController instance; watcher thread uses ~1MB

---

## Security Notes

- Passwords stored as plain text in CSV (not production-ready)
- No encryption on CSV files
- File-based auth has no audit log
- Recommend: Migrate to database with proper auth & encryption

---

## Testing Commands

```powershell
# Build project
mvn clean compile

# Run application
mvn javafx:run

# Run unit tests (if implemented)
mvn clean test

# Generate JAR
mvn clean package
```

---

