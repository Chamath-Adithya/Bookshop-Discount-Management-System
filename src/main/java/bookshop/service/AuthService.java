package bookshop.service;

import java.io.IOException;
import java.util.List;

import bookshop.util.FileHandler;

/**
 * Simple authentication service that validates credentials against data/users.csv
 */
public class AuthService {

    private static final String USERS_FILE = "data/users.csv";

    /**
     * Authenticate a user for a specific role.
     * @param username username
     * @param password password
     * @param requiredRole role to match (case-insensitive). If null, any role accepted.
     * @return true if credentials match and role matches (if provided)
     * @throws IOException if users file cannot be read
     */
    public boolean authenticate(String username, String password, String requiredRole) throws IOException {
        // Input validation
        if (username == null || password == null) {
            System.err.println("[AuthService] Invalid input: username or password is null");
            return false;
        }

        List<String> lines = FileHandler.readCsv(USERS_FILE);
        System.out.println("[AuthService] Read " + lines.size() + " lines from " + USERS_FILE);

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                System.out.println("[AuthService] Skipping empty/comment line: " + line);
                continue;
            }

            String[] cols = line.split(",");
            if (cols.length < 4) {
                System.err.println("[AuthService] Line has insufficient columns (expected 4, got " + cols.length + "): " + line);
                continue;
            }

            String csvUsername = cols[1].trim();
            String csvPassword = cols[2].trim();
            String csvRole = cols[3].trim();

            System.out.println("[AuthService] Comparing - Input: " + username + " vs CSV: " + csvUsername);

            if (csvUsername.equals(username) && csvPassword.equals(password)) {
                System.out.println("[AuthService] Username and password match!");
                if (requiredRole == null) {
                    System.out.println("[AuthService] No role requirement. Authentication successful.");
                    return true;
                }
                boolean roleMatch = false;
                if (requiredRole == null || requiredRole.trim().isEmpty()) {
                    roleMatch = true;
                } else {
                    String req = requiredRole.trim().toUpperCase();
                    String csvR = csvRole.trim().toUpperCase();
                    // Exact match or prefix match (e.g., WORKER1, WORKER_A should match WORKER)
                    roleMatch = csvR.equals(req) || csvR.startsWith(req) || req.startsWith(csvR);
                }
                System.out.println("[AuthService] Role check: CSV role=" + csvRole + ", required=" + requiredRole + ", match=" + roleMatch);
                return roleMatch;
            }
        }

        // Fallback demo accounts
        System.out.println("[AuthService] No match in CSV. Checking fallback demo accounts...");
        if ("admin".equals(username) && "admin".equals(password)) {
            System.out.println("[AuthService] Matched fallback admin account");
            return requiredRole == null || "MANAGER".equalsIgnoreCase(requiredRole);
        }
        if ("user".equals(username) && "user".equals(password)) {
            System.out.println("[AuthService] Matched fallback user account");
            return requiredRole == null || "WORKER".equalsIgnoreCase(requiredRole);
        }

        System.err.println("[AuthService] Authentication failed for user: " + username);
        return false;
    }
}
