package bookshop.service;

import bookshop.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    private Path usersFile;
    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        usersFile = tempDir.resolve("users.csv");
        authService = new AuthService(usersFile.toString());

        // Setup initial user
        String hashedPassword = BCrypt.hashpw("secret", BCrypt.gensalt());
        String line = "u01,admin," + hashedPassword + ",MANAGER";
        FileHandler.writeCsv(usersFile.toString(), Collections.singletonList(line));
    }

    @Test
    void testAuthenticateSuccess() throws IOException {
        assertTrue(authService.authenticate("admin", "secret", "MANAGER"));
    }

    @Test
    void testAuthenticateSuccessAnyRole() throws IOException {
        assertTrue(authService.authenticate("admin", "secret", null));
    }

    @Test
    void testAuthenticateFailureWrongPassword() throws IOException {
        assertFalse(authService.authenticate("admin", "wrong", "MANAGER"));
    }

    @Test
    void testAuthenticateFailureWrongUser() throws IOException {
        assertFalse(authService.authenticate("unknown", "secret", "MANAGER"));
    }

    @Test
    void testAuthenticateFailureWrongRole() throws IOException {
        assertFalse(authService.authenticate("admin", "secret", "WORKER"));
    }

    @Test
    void testAuthenticateNullInput() throws IOException {
        assertFalse(authService.authenticate(null, "secret", "MANAGER"));
        assertFalse(authService.authenticate("admin", null, "MANAGER"));
    }

    @Test
    void testAuthenticateEmptyFile() throws IOException {
        FileHandler.writeCsv(usersFile.toString(), Collections.emptyList());
        assertFalse(authService.authenticate("admin", "secret", "MANAGER"));
    }
}
