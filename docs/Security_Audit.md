# Security Audit & Recommendations

## Identified Vulnerabilities

1.  **Plain Text Passwords**:
    -   **Issue**: Passwords are stored in `data/users.csv` in plain text.
    -   **Risk**: Anyone with access to the file can read all user passwords.
    -   **Evidence**: `AuthService.java` reads the CSV and performs a direct string comparison (`csvPassword.equals(password)`).

2.  **Hardcoded Credentials**:
    -   **Issue**: Fallback accounts (`admin`/`admin` and `user`/`user`) are hardcoded in `AuthService.java`.
    -   **Risk**: These backdoors cannot be changed without recompiling the code and are easily guessable.

3.  **Insecure Data Storage (CSV)**:
    -   **Issue**: CSV files (`customers.csv`, `users.csv`) are standard text files.
    -   **Risk**: They lack built-in access controls, encryption, or integrity checks. A malicious user could easily modify stock levels or prices by editing `products.csv`.

4.  **Logging**:
    -   **Issue**: While explicit password logging wasn't found, processing raw lines from `users.csv` carries a risk of accidental logging during debugging.

## Recommendations

### 1. Implement Password Hashing (Critical)
**Do not store passwords in plain text.**
-   **Solution**: Use a strong hashing algorithm like **BCrypt** or **Argon2**.
-   **Implementation**:
    -   When creating a user, hash the password: `String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());`
    -   Store `hashedPassword` in the CSV.
    -   When logging in, verify the input: `if (BCrypt.checkpw(inputPassword, storedHash)) ...`
-   **Library**: Add `jBCrypt` or similar to your `pom.xml`.

### 2. Remove Hardcoded Accounts (High)
-   **Solution**: Delete the fallback logic in `AuthService.java`. Ensure at least one admin exists in `users.csv` (with a hashed password) during system setup.

### 3. Restrict File Permissions (Medium)
-   **Solution**: Restrict OS-level access to the `data/` directory.
-   **Command**: Run `chmod 600 data/*.csv` (Linux/Mac) to ensure only the file owner (the application runner) can read/write these files.

### 4. Data Encryption (Advanced)
-   **Solution**: If you must stick to CSV, encrypt the files at rest.
-   **Implementation**: Use Java's `Cipher` class (AES) to decrypt the file into memory when the app starts and encrypt it when saving.

### 5. Migrate to a Database (Long-term)
-   **Solution**: Move from CSV to a lightweight embedded database like **SQLite** or **H2**.
-   **Benefits**:
    -   Better security features (user roles, encryption).
    -   ACID compliance (prevents data corruption during concurrent writes).
    -   Standard SQL interface.

## Proposed Next Steps
1.  Add `jBCrypt` dependency.
2.  Create a utility to hash existing passwords in `users.csv`.
3.  Update `AuthService` to use `BCrypt.checkpw`.
4.  Remove hardcoded credentials.
