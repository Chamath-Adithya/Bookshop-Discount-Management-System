package bookshop.controllers;

import java.io.IOException;

import bookshop.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLoginController {
    @FXML
    private TextField username_fld;
    @FXML
    private PasswordField password_fld;
    @FXML
    private Label error_lbl;

    private final AuthService auth = new AuthService();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = username_fld.getText();
        String password = password_fld.getText();
        if (username != null) username = username.trim();
        if (password != null) password = password.trim();
        
        System.out.println("[AdminLoginController] Login attempt - Username: '" + username + "'");
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            error_lbl.setText("Please enter username and password.");
            System.err.println("[AdminLoginController] Missing username or password");
            return;
        }

        try {
            String trimmedUsername = username.trim();
            System.out.println("[AdminLoginController] Authenticating user: '" + trimmedUsername + "' for role MANAGER");
            
            boolean ok = auth.authenticate(trimmedUsername, password, "MANAGER");
            if (!ok) {
                error_lbl.setText("Invalid admin credentials. Please try again.");
                System.err.println("[AdminLoginController] Authentication failed for user: " + trimmedUsername);
                return;
            }

            System.out.println("[AdminLoginController] Authentication successful! Loading Admin Dashboard...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin/Admin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();

        } catch (IOException e) {
            error_lbl.setText("Error during admin login: " + e.getMessage());
            System.err.println("[AdminLoginController] IO Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/RoleSelection.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Select Role");
            stage.show();
        } catch (IOException e) {
            error_lbl.setText("Cannot go back: " + e.getMessage());
        }
    }
}
