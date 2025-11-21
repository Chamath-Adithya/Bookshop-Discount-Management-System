# Quick Reference: Discount System Testing

## Sample Data Available

### Customers (in `data/customers.csv`)
```
Phone: 0789049050  → Yasas (VIP) - Gets 5% discount
Phone: 0756234567  → John Smith (REGULAR) - No tier discount
Phone: 0745678901  → Sarah Johnson (VIP) - Gets 5% discount
Phone: 0765432109  → Michael Brown (REGULAR) - No tier discount
```

### Products with Bulk Discounts (in `data/products.csv`)
```
Product 1 - Pen (Rs. 50/unit)
  ├─ 5+ units  → Rs. 45/unit (saves Rs. 5 per unit)
  └─ 10+ units → Rs. 40/unit (saves Rs. 10 per unit)

Product 2 - Notebook (Rs. 150/unit)
  ├─ 3+ units  → Rs. 130/unit
  └─ 5+ units  → Rs. 120/unit

Product 3 - Pencil (Rs. 20/unit)
  └─ 10+ units → Rs. 15/unit
```

---

## Test Scenario 1: VIP Customer Bulk Purchase

**Cashier Steps**:
1. Enter phone: `0789049050`
2. Click Lookup → Should show "Yasas (VIP)"
3. Select "1 - Pen"
4. Enter quantity: `10`
5. Click "Add to Cart"

**Expected Results**:
- Subtotal: Rs. 500 (10 × 50)
- Product Bulk Discount: Rs. 100 (saves Rs. 10/unit for 10+ qty)
- VIP Discount (5% of subtotal): Rs. 25
- **Total Discount: Rs. 125**
- **Final Price: Rs. 375**

---

## Test Scenario 2: Regular Customer, Standard Quantity

**Cashier Steps**:
1. Enter phone: `0756234567`
2. Click Lookup → Should show "John Smith (REGULAR)"
3. Select "2 - Notebook"
4. Enter quantity: `3`
5. Click "Add to Cart"

**Expected Results**:
- Subtotal: Rs. 450 (3 × 150)
- Product Bulk Discount: Rs. 60 (saves Rs. 20/unit for 3+ qty)
- VIP Discount: Rs. 0 (not VIP)
- **Total Discount: Rs. 60**
- **Final Price: Rs. 390**

---

## Test Scenario 3: Unknown Customer

**Cashier Steps**:
1. Enter phone: `1234567890`
2. Click Lookup → Should show "Not Found"
3. Select any product
4. Enter quantity: `5`
5. Click "Add to Cart"

**Expected Results**:
- Customer Type shows "Not Found"
- Discount info shows "No customer found"
- Bulk discounts STILL apply (no customer tier discount)
- Can proceed with checkout as Regular customer

---

## Test Scenario 4: Admin Adds New Discount

**Admin Steps**:
1. Go to **Discounts** tab
2. Click **Add Discount**
3. Select product "3 - Pencil"
4. Enter quantity: `5`
5. Enter price: `17`
6. Click OK/Submit

**Expected Results**:
- New discount row added: "3 - Pencil", qty 5, price 17
- Can now delete this discount using Delete button
- Cashier's file watcher picks up change (or click Refresh)
- Cashier now applies 5+ pencil discount

---

## Admin Discount Management

### Adding a Discount
1. **Discounts** tab → **Add Discount**
2. **Step 1**: Select product from dropdown
3. **Step 2**: Enter minimum quantity (e.g., 5)
4. **Step 3**: Enter discounted price (e.g., 45)
5. Done! Discount appears in table and applies immediately in cashier

### Deleting a Discount
1. **Discounts** tab → Find discount row
2. Click **Delete** button in Actions column
3. Discount removed from product
4. Cashier auto-refreshes (or click Refresh)

### Editing a Discount
- Delete old discount + Add new one with same quantity threshold

---

## Common Discount Scenarios

| Scenario | Product | Qty | VIP | Subtotal | Bulk Disc | VIP Disc | Total |
|----------|---------|-----|-----|----------|-----------|----------|-------|
| Pen 5+ (Regular) | 1 | 5 | No | 250 | 25 | 0 | **225** |
| Pen 5+ (VIP) | 1 | 5 | Yes | 250 | 25 | 12.50 | **212.50** |
| Pen 10+ (Regular) | 1 | 10 | No | 500 | 100 | 0 | **400** |
| Pen 10+ (VIP) | 1 | 10 | Yes | 500 | 100 | 25 | **375** |
| Notebook 3+ (VIP) | 2 | 3 | Yes | 450 | 60 | 22.50 | **367.50** |
| Pencil 1-9 (Regular) | 3 | 7 | No | 140 | 0 | 0 | **140** |
| Pencil 10+ (VIP) | 3 | 10 | Yes | 200 | 50 | 10 | **140** |

---

## Troubleshooting

### Discount not showing in cashier?
1. Click **Refresh Products** button
2. Check that discount is in **Discounts** tab in admin
3. Verify CSV format: `5:45.00` (quantity:price)

### Customer lookup not working?
1. Check phone number format matches CSV exactly
2. Verify customer exists in **Customers** tab
3. Make sure phone field is not blank in CSV

### Discount calculation wrong?
1. Verify product has bulk discount rules in CSV
2. Check customer type (VIP = 5%, REGULAR = 0%)
3. Remember: only HIGHEST applicable bulk discount is used
   - If product has both 5:price and 10:price, and qty=10, only 10:price applies

---

## Key Behaviors

✅ **Bulk discounts**: Only highest applicable threshold is used
✅ **VIP discounts**: Always 5%, stacks with bulk discount
✅ **Regular customers**: No tier discount, only bulk discounts apply
✅ **Rounding**: All prices formatted to 2 decimal places
✅ **Unknown customers**: Can checkout as Regular (no tier discount)
✅ **Synchronization**: Changes visible within 300ms or click Refresh

---

## Files to Check

- `data/products.csv` - Product list with discount format: `qty1:price1;qty2:price2`
- `data/customers.csv` - Customer list with phone for lookup
- Admin logs show discount operations in console
- Cashier logs show product loading and discount calculations

