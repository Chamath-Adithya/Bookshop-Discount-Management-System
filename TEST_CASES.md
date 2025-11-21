# Discount System Test Cases & Expected Results

**Last Updated**: November 21, 2025  
**Build Status**: ✅ SUCCESS

---

## Test Environment Setup

### Before Running Tests:
1. Build project: `mvn clean compile`
2. Verify data files exist:
   - `data/products.csv` (with sample products + discounts)
   - `data/customers.csv` (with sample customers)
   - `data/users.csv` (with admin/cashier accounts)
3. Startup credentials:
   - Admin: `admin` / `admin123`
   - Cashier: `yasaru` / `yasaru123`

---

## ADMIN SIDE TESTS

### Test A1: Add Product Bulk Discount

**Objective**: Verify admin can add bulk discount to existing product

**Steps**:
1. Login as Admin (admin/admin123)
2. Click "Discounts" in sidebar
3. Click "Add Discount" button
4. Dialog Step 1: Select "1 - Pen" from dropdown
5. Dialog Step 2: Enter quantity `8`
6. Dialog Step 3: Enter price `42`
7. Click OK/Confirm

**Expected Result** ✅:
- Dialog closes
- Discounts table now shows new row:
  - Code: "1 - Pen"
  - Type (Qty): "8"
  - Value (Price): "42.00"
- Message appears: "Discount added successfully!"

**File Verification**:
- Open `data/products.csv`
- Product 1 line should include: `"5:45.00;10:40.00;8:42.00"`

---

### Test A2: View Discount in Table

**Objective**: Verify all discounts display correctly

**Steps**:
1. In Admin, go to "Discounts" tab
2. Observe the table

**Expected Result** ✅:
- Should see rows for each discount:
  1. "1 - Pen", qty 5, price 45.00
  2. "1 - Pen", qty 10, price 40.00
  3. "1 - Pen", qty 8, price 42.00 (from A1, if done)
  4. "2 - Notebook", qty 3, price 130.00
  5. "2 - Notebook", qty 5, price 120.00
  6. "3 - Pencil", qty 10, price 15.00

---

### Test A3: Delete Product Discount

**Objective**: Verify admin can remove discounts

**Steps**:
1. In Discounts table, find "3 - Pencil" row (qty 10, price 15)
2. Click "Delete" button on that row
3. Confirm deletion

**Expected Result** ✅:
- Row disappears from table
- Message: "Discount deleted successfully!"
- Pencil product now has no bulk discounts

**File Verification**:
- Open `data/products.csv`
- Product 3 discounts field should be empty: `""`

---

### Test A4: Add Customer

**Objective**: Verify admin can add new customer

**Steps**:
1. Click "Customers" in sidebar
2. Click "Add Customer" button (if implemented)
3. Enter: ID=C5, Name=Ahmed Hassan, Type=VIP, Phone=0787654321
4. Confirm

**Expected Result** ✅:
- New customer appears in table
- File `data/customers.csv` updated with new row

---

### Test A5: Edit Customer

**Objective**: Verify admin can modify customer

**Steps**:
1. In Customers table, select "C2 - John Smith"
2. Click "Edit" button
3. Change phone to `0767777777`
4. Change type from REGULAR to VIP
5. Confirm

**Expected Result** ✅:
- Customer table updates immediately
- `data/customers.csv` shows new phone and type
- Next cashier lookup on this customer shows VIP

---

### Test A6: Delete Customer

**Objective**: Verify admin can remove customer

**Steps**:
1. In Customers table, find customer to delete
2. Click "Delete" button
3. Confirm

**Expected Result** ✅:
- Customer removed from table
- Removed from `data/customers.csv`

---

### Test A7: View Products

**Objective**: Verify admin can see products with discounts

**Steps**:
1. Click "Products" in sidebar
2. View Products table

**Expected Result** ✅:
- Table shows 3 products:
  1. Product 1: Pen, Rs. 50, Qty 100
  2. Product 2: Notebook, Rs. 150, Qty 50
  3. Product 3: Pencil, Rs. 20, Qty 80

---

## CASHIER SIDE TESTS

### Test C1: VIP Customer Lookup - Found

**Objective**: Verify cashier can lookup VIP customer by phone

**Steps**:
1. Login as Cashier (yasaru/yasaru123)
2. In "Customer Information" section, enter phone: `0789049050`
3. Click "Lookup" button
4. Wait for lookup to complete

**Expected Result** ✅:
- Customer Type field shows: "VIP"
- Message appears: "Customer found: Yasas"
- Discount text shows: "VIP Customer: 5% discount applied" (in green)

---

### Test C2: Regular Customer Lookup - Found

**Objective**: Verify cashier can lookup Regular customer

**Steps**:
1. In Customer Information, enter phone: `0756234567`
2. Click "Lookup"

**Expected Result** ✅:
- Customer Type field shows: "REGULAR"
- Message appears: "Customer found: John Smith"
- Discount text shows: "Regular Customer: No additional discount"

---

### Test C3: Unknown Customer - Not Found

**Objective**: Verify system handles missing customer gracefully

**Steps**:
1. In Customer Information, enter phone: `9999999999`
2. Click "Lookup"

**Expected Result** ✅:
- Customer Type shows: "Not Found"
- Discount text shows: "No customer found with this phone number" (in red)
- Warning message: "Customer not found. Proceeding as Regular customer."
- Cashier can continue (proceeds as Regular customer)

---

### Test C4: Clear Customer Field

**Objective**: Verify cashier can clear customer selection

**Steps**:
1. After lookup, select new customer OR clear phone field
2. Click "Lookup" again with new phone

**Expected Result** ✅:
- Previous customer info cleared
- New customer info displayed

---

### Test C5: Add Regular Customer Product - No Bulk Discount

**Objective**: Verify discount calculation for regular customer without bulk discount

**Setup**: Lookup customer 0756234567 (John Smith, REGULAR)

**Steps**:
1. From products grid, click "Select" on Pen
2. Enter quantity: `3`
3. Click "Add to Cart"

**Expected Result** ✅:
- Product selected: "Pen"
- Price shown: "Rs. 50.00"
- Cart shows item:
  - Name: Pen
  - Qty: 3 × Rs. 50.00 = Rs. 150.00
  - No discount line (qty 3 doesn't meet bulk threshold of 5)
- Subtotal: Rs. 150
- Discount: Rs. 0
- Total: Rs. 150

---

### Test C6: Add VIP Customer Product - With Bulk Discount

**Objective**: Verify discount calculation for VIP customer with bulk discount

**Setup**: Clear cart, lookup customer 0789049050 (Yasas, VIP)

**Steps**:
1. Select "Pen" from products
2. Enter quantity: `5`
3. Click "Add to Cart"

**Expected Result** ✅:
- Cart shows:
  - Pen: 5 × Rs. 50.00 = Rs. 250.00
  - Discount line: "(VIP Customer) - Save Rs. 25.00" (VIP 5%)
  - Plus additional: Bulk discount for 5+ units (Rs. 25 saved)
- Subtotal: Rs. 250.00
- Total Discount: Rs. 50.00 (Rs. 25 VIP + Rs. 25 bulk)
- Total: Rs. 200.00

**Calculation Breakdown**:
- Bulk discount (5 units at Rs. 45 instead of 50): 5 × (50-45) = Rs. 25
- VIP discount (5% of subtotal): 250 × 0.05 = Rs. 12.50
- Total savings: Rs. 37.50
- **Final price: Rs. 212.50**

---

### Test C7: Add Multiple Products with Mixed Discounts

**Objective**: Verify cart correctly handles multiple items with different discount rules

**Setup**: Lookup customer 0745678901 (Sarah Johnson, VIP)

**Steps**:
1. Add Pen × 10 to cart
2. Add Notebook × 5 to cart
3. Add Pencil × 3 to cart

**Expected Results** ✅:

**Item 1 - Pen (10 units)**:
- Subtotal: 10 × 50 = Rs. 500
- Bulk discount: 10 units at Rs. 40 each = 10 × (50-40) = Rs. 100
- VIP discount: 5% of Rs. 500 = Rs. 25
- Item total: Rs. 375

**Item 2 - Notebook (5 units)**:
- Subtotal: 5 × 150 = Rs. 750
- Bulk discount: 5 units at Rs. 120 each = 5 × (150-120) = Rs. 150
- VIP discount: 5% of Rs. 750 = Rs. 37.50
- Item total: Rs. 562.50

**Item 3 - Pencil (3 units)**:
- Subtotal: 3 × 20 = Rs. 60
- Bulk discount: None (minimum 10 units) = Rs. 0
- VIP discount: 5% of Rs. 60 = Rs. 3
- Item total: Rs. 57

**Cart Summary**:
- Subtotal: Rs. 1,310.00
- Total Discount: Rs. 315.50
- **Total: Rs. 994.50**

---

### Test C8: Checkout Process

**Objective**: Verify checkout completes successfully

**Steps**:
1. With items in cart, click "Proceed to Pay"
2. Confirm payment

**Expected Result** ✅:
- Confirmation dialog shows total amount
- Click OK to confirm
- Message: "Payment successful! Thank you for your purchase."
- Bill file created in `bills/` folder
- Cart cleared
- Return to product selection

---

### Test C9: Clear Cart

**Objective**: Verify cart can be cleared

**Steps**:
1. Add items to cart
2. Click "Clear Cart"
3. Confirm

**Expected Result** ✅:
- All items removed
- Cart empty
- Subtotal: Rs. 0.00
- Discount: Rs. 0.00
- Total: Rs. 0.00

---

### Test C10: Product Refresh

**Objective**: Verify manual refresh syncs with admin changes

**Steps**:
1. In Cashier, note current products displayed
2. In Admin, add new discount to a product (e.g., Pen)
3. In Cashier, click "Refresh Products"
4. Observe products grid

**Expected Result** ✅:
- Products grid re-renders
- New discount appears in product card discount info
- Takes effect immediately in cart calculations

---

## SYNCHRONIZATION TESTS

### Test S1: Real-time File Watcher Detection

**Objective**: Verify cashier detects admin changes within 300ms

**Setup**: 
- Both Admin and Cashier running side-by-side
- Cashier has products displayed

**Steps**:
1. In Admin, add new discount to Pencil (e.g., qty 7 = Rs. 16.50)
2. Immediately look at Cashier's Pencil product card
3. Within 300ms, product card should update

**Expected Result** ✅:
- No action needed in Cashier (automatic)
- Product card shows new discount: "Discounts: 7x: Rs16.50; 10x: Rs15.00"

---

### Test S2: Persistence Across Restart

**Objective**: Verify changes survive application restart

**Steps**:
1. In Admin, add discount: Pen qty 6 = Rs. 44
2. Close both Admin and Cashier
3. Wait 5 seconds
4. Restart both applications
5. In Cashier, check Pen product card

**Expected Result** ✅:
- Discount still shows in Cashier
- Verify in `data/products.csv` that discount persists

---

## EDGE CASE TESTS

### Test E1: Invalid Phone Format

**Steps**:
1. Enter phone: `abc123xyz`
2. Click Lookup

**Expected Result** ✅:
- Shows "Not Found"
- Allows proceed as Regular customer

---

### Test E2: Empty Phone Field

**Steps**:
1. Leave phone field empty
2. Click Lookup

**Expected Result** ✅:
- Warning: "Please enter a phone number!"
- Lookup not performed

---

### Test E3: Quantity Zero

**Steps**:
1. Select product
2. Enter quantity: `0`
3. Click Add to Cart

**Expected Result** ✅:
- Warning: "Quantity must be greater than 0!"

---

### Test E4: Quantity Exceeds Stock

**Steps**:
1. Select Pen (stock: 100)
2. Enter quantity: `150`
3. Click Add to Cart

**Expected Result** ✅:
- Warning: "Insufficient stock! Available: 100"

---

### Test E5: Invalid Discount Price

**Steps** (Admin):
1. Click Add Discount
2. Select product
3. Enter quantity: `5`
4. Enter price: `abc`
5. Click OK

**Expected Result** ✅:
- Error: "Invalid price format."
- Dialog remains open for correction

---

### Test E6: Duplicate Quantity Threshold

**Steps** (Admin):
1. Add discount Pen qty 5 = Rs. 45 (already exists)
2. Add another discount Pen qty 5 = Rs. 44

**Expected Result** ✅:
- New discount overwrites old one (qty 5 now = Rs. 44)
- Verify in CSV

---

## PERFORMANCE TESTS

### Test P1: Large Cart

**Objective**: Verify system handles many items

**Steps**:
1. Lookup customer
2. Add 50 items of same product to cart

**Expected Result** ✅:
- Cart updates smoothly
- Calculations complete in < 1 second
- Display renders without lag

---

### Test P2: Product Refresh Performance

**Objective**: Verify refresh doesn't cause lag

**Steps**:
1. Click Refresh Products repeatedly (5x quickly)

**Expected Result** ✅:
- No crashes
- Products reload cleanly each time

---

## DATA INTEGRITY TESTS

### Test D1: CSV Consistency

**Steps**:
1. Make changes in Admin (add/edit/delete)
2. Immediately open `data/products.csv` in text editor
3. Verify format

**Expected Result** ✅:
- Discount format: `qty1:price1;qty2:price2`
- All prices have 2 decimal places
- No extra whitespace

---

### Test D2: Customer CSV Consistency

**Steps**:
1. Add/edit customer in Admin
2. Open `data/customers.csv`

**Expected Result** ✅:
- Format: `id,name,type,phone`
- Phone included even if empty
- No corrupted lines

---

## CONCLUSION

All tests should pass. If any test fails:

1. **Check console logs** for error messages
2. **Verify data files** exist and have correct format
3. **Restart applications** to clear any cached state
4. **Verify sample data** is properly loaded from CSV files

---

