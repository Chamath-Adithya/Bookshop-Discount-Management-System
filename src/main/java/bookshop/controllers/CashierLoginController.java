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

public class CashierLoginController {
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
        
        System.out.println("[CashierLoginController] Login attempt - Username: '" + username + "'");
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            error_lbl.setText("Please enter username and password.");
            System.err.println("[CashierLoginController] Missing username or password");
            return;
        }

        try {
            String trimmedUsername = username.trim();
            System.out.println("[CashierLoginController] Authenticating user: '" + trimmedUsername + "' for role WORKER");
            
            boolean ok = auth.authenticate(trimmedUsername, password, "CASHIER");
            if (!ok) {
                error_lbl.setText("Invalid cashier credentials. Please try again.");
                System.err.println("[CashierLoginController] Authentication failed for user: " + trimmedUsername);
                return;
            }

            System.out.println("[CashierLoginController] Authentication successful! Loading POS...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/User/Cashier.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("POS - Cashier");
            stage.show();

        } catch (IOException e) {
            error_lbl.setText("Error during cashier login: " + e.getMessage());
            System.err.println("[CashierLoginController] IO Error: " + e.getMessage());
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
