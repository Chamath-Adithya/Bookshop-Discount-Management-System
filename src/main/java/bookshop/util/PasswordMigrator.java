package bookshop.util;

import org.mindrot.jbcrypt.BCrypt;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PasswordMigrator {
    private static final String USERS_FILE = "data/users.csv";

    public static void main(String[] args) {
        System.out.println("Starting password migration...");
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            List<String> newLines = new ArrayList<>();
            boolean headerProcessed = false;
            int migratedCount = 0;

            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    newLines.add(line);
                    continue;
                }

                if (!headerProcessed && line.toLowerCase().contains("username")) {
                    newLines.add(line);
                    headerProcessed = true;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String userId = parts[0].trim();
                    String username = parts[1].trim();
                    String password = parts[2].trim();
                    String role = parts[3].trim();

                    // Check if already hashed (BCrypt hashes start with $2a$)
                    if (!password.startsWith("$2a$")) {
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                        newLines.add(String.join(",", userId, username, hashedPassword, role));
                        migratedCount++;
                        System.out.println("Migrated user: " + username);
                    } else {
                        newLines.add(line);
                        System.out.println("Skipping already hashed user: " + username);
                    }
                } else {
                    newLines.add(line);
                }
            }

            if (migratedCount > 0) {
                Files.write(Paths.get(USERS_FILE), newLines);
                System.out.println("Migration complete. " + migratedCount + " passwords hashed.");
            } else {
                System.out.println("No passwords needed migration.");
            }

        } catch (IOException e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
