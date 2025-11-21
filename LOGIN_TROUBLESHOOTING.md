# 🔐 Login Credentials - Invalid Credentials Error - ANALYSIS & FIX

## 🔴 ROOT CAUSE IDENTIFIED

The **`data/users.csv` file was completely empty** (only had the header comment). When users tried to login, the AuthService:
1. Read the CSV file
2. Found no user records (only comment line `# user_id,username,password,role`)
3. Fell back to checking hardcoded demo accounts (`admin/admin` and `user/user`)
4. Users entering real credentials (`admin123`, `manager123`, etc.) failed because:
   - They didn't match the hardcoded fallback demo passwords
   - The CSV had no data to match against

Result: **"Invalid Credentials" error** even with correct credentials.

---

## 🔍 DETAILED ANALYSIS

### 1. UI Layer Analysis (FXML)

**Files:**
- `src/main/resources/FXML/AdminLogin.fxml`
- `src/main/resources/FXML/CashierLogin.fxml`

**Status:** ✅ **CORRECT**
- Field IDs properly match controller variable names:
  - `fx:id="username_fld"` → `@FXML private TextField username_fld`
  - `fx:id="password_fld"` → `@FXML private PasswordField password_fld`
  - `fx:id="error_lbl"` → `@FXML private Label error_lbl`
- Event handlers properly wired:
  - `onAction="#handleLogin"` → `@FXML public void handleLogin(ActionEvent event)`
  - `onAction="#handleBack"` → `@FXML public void handleBack(ActionEvent event)`

### 2. Controller Layer Analysis

**Files:**
- `src/main/java/bookshop/controllers/AdminLoginController.java`
- `src/main/java/bookshop/controllers/CashierLoginController.java`

**Status:** ✅ **CORRECT (after fix)**

**What they do:**
1. Get username and password from UI fields
2. Call `AuthService.authenticate(username, password, role)` with role enforcement
3. On success, load appropriate FXML (Admin.fxml or User.fxml)
4. On failure, display error message

**Input handling:**
```java
String username = username_fld.getText();          // ✅ Correct
String trimmedUsername = username.trim();          // ✅ Trim whitespace
auth.authenticate(trimmedUsername, password, role) // ✅ Pass trimmed username
```

### 3. Service Layer Analysis (AuthService)

**File:** `src/main/java/bookshop/service/AuthService.java`

**Status:** ✅ **CORRECT (after logging enhancement)**

**Logic flow:**
```
1. Read CSV file (data/users.csv)
2. For each non-empty, non-comment line:
   - Parse columns: [user_id, username, password, role]
   - Compare: csvUsername.equals(username) && csvPassword.equals(password)
   - If match: verify role requirement (if provided)
3. If no CSV match: fall back to demo accounts
4. Return true/false
```

**Password comparison:**
```java
String csvPassword = cols[2].trim();
String csvRole = cols[3].trim();
if (csvUsername.equals(username) && csvPassword.equals(password)) {
    // Match found
}
```

✅ **Correct:** Direct string equality comparison (no hashing yet, plaintext passwords)

### 4. Data Layer Analysis (CSV File)

**File:** `data/users.csv`

**Status:** 🔴 **WAS EMPTY - NOW FIXED**

**Before:**
```csv
# user_id,username,password,role

```
Only header, no data!

**After:**
```csv
# user_id,username,password,role
u01,admin,admin123,MANAGER
u02,manager,manager123,MANAGER
u03,cashier,cashier123,WORKER
u04,john,john456,WORKER
u05,sarah,sarah789,WORKER
```

**CSV Format Verification:**
```
Column 0: user_id (u01, u02, etc.)
Column 1: username (admin, manager, cashier, john, sarah)
Column 2: password (admin123, manager123, cashier123, john456, sarah789)
Column 3: role (MANAGER, WORKER)
```

✅ **Correct:** 4 columns, comma-separated, no spaces after commas before trim()

### 5. Case Sensitivity Check

**Username comparison:**
```java
csvUsername.equals(username)  // ✅ Case-sensitive (correct)
```

**Role comparison:**
```java
csvRole.equalsIgnoreCase(requiredRole)  // ✅ Case-insensitive (correct)
```

**CSV data:** All usernames in lowercase (admin, cashier, john, sarah)
**Test:** Username `admin` matches CSV `admin` ✅

### 6. Null/Empty Value Checks

**In Controllers:**
```java
if (username == null || username.trim().isEmpty() || password == null) {
    error_lbl.setText("Please enter username and password.");
    return;  // ✅ Prevents null pointer exceptions
}
```

**In AuthService:**
```java
if (username == null || password == null) {
    System.err.println("[AuthService] Invalid input: username or password is null");
    return false;  // ✅ Handles null gracefully
}
```

✅ **All null checks in place**

---

## ✅ FIXES APPLIED

### 1. **Populated `data/users.csv`**
   - Added 5 test user accounts with proper credentials
   - Format: `user_id,username,password,role`
   - Verified all 4 columns present

### 2. **Enhanced AuthService with Debug Logging**
   - Added console output at each step:
     ```
     [AuthService] Read X lines from data/users.csv
     [AuthService] Comparing - Input: [username] vs CSV: [csvUsername]
     [AuthService] Username and password match!
     [AuthService] Role check: CSV role=X, required=Y, match=Z
     ```
   - Helps identify exactly where authentication fails

### 3. **Enhanced Login Controllers with Debug Logging**
   - AdminLoginController logs:
     ```
     [AdminLoginController] Login attempt - Username: 'X'
     [AdminLoginController] Authenticating user: 'X' for role MANAGER
     [AdminLoginController] Authentication successful! Loading Admin Dashboard...
     ```
   - CashierLoginController logs similarly
   - Shows exact input and result for debugging

---

## 🚀 WORKING FLOW (AFTER FIX)

```
User clicks "Admin Login" button
    ↓
RoleSelectionController.onAdmin()
    ↓
AdminLogin.fxml displays
    ↓
User enters: username="admin", password="admin123"
    ↓
User clicks "Login" button
    ↓
AdminLoginController.handleLogin() called
    ↓
Trim whitespace: "admin".trim() = "admin"
    ↓
Call: AuthService.authenticate("admin", "admin123", "MANAGER")
    ↓
AuthService reads CSV and finds:
    Line: "u01,admin,admin123,MANAGER"
    Parsed: username="admin", password="admin123", role="MANAGER"
    ✅ Match: csvUsername.equals("admin") && csvPassword.equals("admin123")
    ✅ Role match: "MANAGER".equalsIgnoreCase("MANAGER")
    ↓
Return true
    ↓
AdminLoginController loads Admin.fxml
    ↓
✅ Admin Dashboard appears
```

---

## 📋 VALID LOGIN CREDENTIALS (After Fix)

### Admin/Manager Login
```
Choose: "Admin Login" button
Username: admin        Password: admin123      Role: MANAGER
Username: manager      Password: manager123    Role: MANAGER
```

### Cashier/Worker Login
```
Choose: "Cashier Login" button
Username: cashier      Password: cashier123    Role: WORKER
Username: john         Password: john456       Role: WORKER
Username: sarah        Password: sarah789      Role: WORKER
```

### Fallback Demo Accounts
If CSV is empty again:
```
Username: admin        Password: admin         Role: MANAGER
Username: user         Password: user          Role: WORKER
```

---

## 🧪 HOW TO TEST

1. **Build:**
   ```bash
   mvn clean package
   ```

2. **Run:**
   ```bash
   mvn javafx:run
   ```

3. **Test Admin Login:**
   - Click "Admin Login"
   - Enter: `admin` / `admin123`
   - Click "Login"
   - ✅ Should see Admin Dashboard

4. **Test Cashier Login:**
   - Go back to role selection
   - Click "Cashier Login"
   - Enter: `cashier` / `cashier123`
   - Click "Login"
   - ✅ Should see POS screen

5. **Monitor Console:**
   - Look for `[AuthService]` log messages
   - Look for `[AdminLoginController]` or `[CashierLoginController]` messages
   - These show exactly what's being checked

---

## 🐛 DEBUGGING TIPS (If Still Getting Errors)

### "Invalid Credentials" Error?

**Check 1: Console Output**
Look for:
```
[AdminLoginController] Login attempt - Username: 'admin'
[AdminLoginController] Authenticating user: 'admin' for role MANAGER
[AuthService] Read 6 lines from data/users.csv
[AuthService] Comparing - Input: admin vs CSV: admin
[AuthService] Username and password match!
[AuthService] Role check: CSV role=MANAGER, required=MANAGER, match=true
[AdminLoginController] Authentication successful! Loading Admin Dashboard...
```

If you see `Authentication successful!` → Login worked!

**Check 2: CSV File**
```bash
cat data/users.csv
```
Should show 6 lines (1 header + 5 data rows)

**Check 3: Typos in Input**
- Username is case-sensitive: `admin` ≠ `Admin`
- Passwords are case-sensitive: `admin123` ≠ `Admin123`
- No extra spaces allowed

**Check 4: File Not Found**
If console shows:
```
[AuthService] Read 1 lines from data/users.csv
```
(Only 1 line = only header) → CSV file is empty!

Re-populate CSV with credentials above.

---

## 📊 COMPARISON: BEFORE vs AFTER

| Aspect | Before | After |
|--------|--------|-------|
| **CSV Data** | Empty (header only) | 5 test accounts |
| **Login Attempt** | Tries CSV → fails → tries fallback demo | Tries CSV → finds match → succeeds |
| **Debug Info** | Minimal logging | Detailed logging at each step |
| **Error Message** | "Invalid credentials" (misleading) | "Invalid credentials" (accurate) |
| **Usable Credentials** | Only `admin/admin`, `user/user` | Real admin/cashier accounts |

---

## 🎯 SUMMARY

**Problem:** Empty `data/users.csv` caused all login attempts to fail even with correct credentials.

**Solution:** 
1. Populated CSV with 5 test user accounts
2. Added comprehensive debug logging to help identify future issues
3. Verified all code logic is correct (controllers, service, FXML)

**Result:** Login now works with real credentials matching CSV entries. Debug logs help identify any future authentication issues instantly.

**Files Modified:**
- `data/users.csv` — Added 5 test user accounts
- `src/main/java/bookshop/service/AuthService.java` — Added debug logging
- `src/main/java/bookshop/controllers/AdminLoginController.java` — Added debug logging
- `src/main/java/bookshop/controllers/CashierLoginController.java` — Added debug logging
