# Cashier Fix - Testing Guide

## Quick Start

### Build & Run
```powershell
cd "D:\Java Pro\Bookshop-Discount-Management-System"
mvn clean compile
mvn javafx:run
```

---

## Test Scenarios

### Test 1: Dynamic Product Loading
**Purpose**: Verify that all products from CSV load dynamically in cashier grid

**Steps**:
1. Run application: `mvn javafx:run`
2. Click "Cashier" button on role selection screen
3. Login with credentials: `cashier` / `cashier123`
4. **VERIFY**: 
   - ✅ Cashier dashboard displays with products grid
   - ✅ Product grid shows ALL products from `data/products.csv` (currently: Pen, Pencil)
   - ✅ Each product card displays:
     - Product name (e.g., "Pen", "Pencil")
     - Product price in Rs. format (e.g., "Rs. 100.00")
     - "Add to Cart" button
   - ✅ Products arranged in 2-column grid layout

---

### Test 2: Add to Cart Functionality
**Purpose**: Verify that clicking "Add to Cart" properly adds items to shopping cart

**Steps**:
1. From cashier dashboard (after Test 1)
2. Click "Add to Cart" button on any product (e.g., Pen)
3. **VERIFY**:
   - ✅ Success alert appears: "Pen added to cart!"
   - ✅ Cart on right side shows the item:
     - Item name
     - Quantity (starts at 1)
     - Subtotal
   - ✅ Total amount updates correctly
4. Click "Add to Cart" on same product again
5. **VERIFY**:
   - ✅ Quantity of Pen increases to 2
   - ✅ Subtotal recalculates (e.g., 100 × 2 = 200)
   - ✅ Total amount increases

---

### Test 3: Multiple Products in Cart
**Purpose**: Verify shopping cart handles multiple different products

**Steps**:
1. From cashier dashboard
2. Click "Add to Cart" on Pen (product p01)
3. Click "Add to Cart" on Pencil (product p02)
4. **VERIFY**:
   - ✅ Both items appear in cart
   - ✅ Each item shows correct name, quantity, and subtotal
   - ✅ Total amount = sum of all subtotals
   - ✅ Cart UI displays all items properly

---

### Test 4: Clear Cart Button
**Purpose**: Verify clear cart removes all items

**Steps**:
1. From cashier dashboard with items in cart (after Test 3)
2. Click "Clear" button
3. **VERIFY**:
   - ✅ Info alert appears: "Cart cleared successfully"
   - ✅ Cart becomes empty
   - ✅ Total amount resets to 0
   - ✅ Product grid still displays all products

---

### Test 5: Admin-Added Products Visibility
**Purpose**: Verify that products added by admin appear in cashier (after app restart)

**Prerequisites**: 
- Create a new product via Admin dashboard before running this test

**Steps**:
1. While app is running, click "Admin" button
2. Login with credentials: `admin` / `admin123`
3. Go to "Products" tab
4. Add a new product:
   - ID: `p03`
   - Name: `Notebook`
   - Price: `150.0`
   - Discounts: `5:145.0` (5+ items → Rs.145)
5. Click "Add Product" button
6. **Verify product saved**: Check that new product appears in Products table
7. Click "Logout" button
8. Now log back in as Cashier (`cashier` / `cashier123`)
9. **VERIFY**:
   - ⚠️ **Note**: Product will appear after app restart
   - ✅ New product "Notebook" appears in cashier grid
   - ✅ Price displays correctly: "Rs. 150.00"
   - ✅ "Add to Cart" button works for new product

---

### Test 6: Payment Process
**Purpose**: Verify checkout and receipt generation

**Steps**:
1. From cashier dashboard with items in cart
2. Click "Pay" button
3. **VERIFY**:
   - ✅ Payment dialog/alert appears
   - ✅ Shows total amount due
   - ✅ Receipt details displayed:
     - List of items with quantities and prices
     - Discount applied (if applicable)
     - Final total amount
   - ✅ After payment, cart clears
   - ✅ New receipt generated with timestamp

---

### Test 7: Button Event Handling
**Purpose**: Verify all buttons respond correctly to clicks

**Steps**:
1. From cashier dashboard
2. Test each button:
   - **Search button**: Click on search button (currently placeholder)
     - ✅ No error occurs
   - **Logout button**: Click logout
     - ✅ Returns to role selection screen
   - **Clear Cart button**: Click with items in cart
     - ✅ Cart clears as expected
   - **Pay button**: Click with items in cart
     - ✅ Payment process initiates

---

## Expected Product Data

### From `data/products.csv`:
```csv
id,name,price,discounts
p01,Pen,100.0,5:95.0;10:80.0
p02,Pencil,40.0,3:35.0;10:30.0;100:25.0
```

**Note**: After adding products via Admin, they'll appear in cashier on next app launch.

---

## Compilation Verification

Ensure build is clean:
```
mvn clean compile
[INFO] BUILD SUCCESS
[INFO] Total time: ~5 seconds
```

---

## Common Issues & Solutions

### Issue: Products don't appear in grid
**Solution**: 
- Check that `data/products.csv` file exists and has valid product data
- Ensure ProductService is initialized in `initialize()` method
- Check console for error messages: `[UserController]`

### Issue: "Add to Cart" button doesn't work
**Solution**:
- Verify `handleAddToCart(ActionEvent event)` signature matches FXML `onAction` binding
- Check that product card UserData contains valid product ID
- Verify ProductService can find product by ID

### Issue: App crashes on startup
**Solution**:
- Check console for exceptions
- Verify all FXML files are in correct locations
- Check that CSV files have proper format (comma-separated)

### Issue: Cart shows items but total doesn't calculate
**Solution**:
- Check that `totalAmountText` fx:id exists in FXML
- Verify `updateCartDisplay()` method is being called after adding items
- Check price values in products.csv are valid numbers

---

## Regression Testing

After any code changes, verify:
1. ✅ Project compiles: `mvn clean compile` succeeds
2. ✅ App launches: `mvn javafx:run` opens without errors
3. ✅ Products load: Grid shows all products from CSV
4. ✅ Add to cart works: Items properly add to cart
5. ✅ All buttons respond: No unresponsive buttons
6. ✅ Admin-cashier sync: New admin products visible after restart

---

## Performance Expectations

- **Startup time**: < 5 seconds for app to fully load
- **Product grid display**: < 1 second to populate grid with 10 products
- **Add to cart response**: Immediate (< 100ms)
- **Search response**: < 500ms for filtering 100+ products

---

## Success Criteria

✅ All tests pass above
✅ No compilation errors
✅ No runtime exceptions in console
✅ Clean shutdown without errors
✅ Admin-cashier data flow works end-to-end
