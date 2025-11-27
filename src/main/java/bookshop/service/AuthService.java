package bookshop.service;

import java.io.IOException;
import java.util.List;

import bookshop.util.FileHandler;

import org.mindrot.jbcrypt.BCrypt;

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
                continue;
            }

            String[] cols = line.split(",");
            if (cols.length < 4) {
                continue;
            }

            String csvUsername = cols[1].trim();
            String csvPasswordHash = cols[2].trim();
            String csvRole = cols[3].trim();

            if (csvUsername.equals(username)) {
                // Verify password using BCrypt
                boolean passwordMatch = false;
                try {
                    passwordMatch = BCrypt.checkpw(password, csvPasswordHash);
                } catch (IllegalArgumentException e) {
                    System.err.println("[AuthService] Invalid hash for user " + username + ", likely plain text. Please migrate passwords.");
                    // Fallback for plain text (temporary/legacy support if needed, but better to fail secure)
                    // passwordMatch = csvPasswordHash.equals(password); 
                    return false; 
                }

                if (passwordMatch) {
                    System.out.println("[AuthService] Password match for user: " + username);
                    if (requiredRole == null) {
                        return true;
                    }
                    
                    String req = requiredRole.trim().toUpperCase();
                    String csvR = csvRole.trim().toUpperCase();
                    // Exact match or prefix match
                    boolean roleMatch = csvR.equals(req) || csvR.startsWith(req) || req.startsWith(csvR);
                    
                    if (!roleMatch) {
                        System.out.println("[AuthService] Role mismatch. Required: " + requiredRole + ", Found: " + csvRole);
                    }
                    return roleMatch;
                } else {
                    System.out.println("[AuthService] Password mismatch for user: " + username);
                }
            }
        }

        System.err.println("[AuthService] Authentication failed for user: " + username);
        return false;
    }
}
