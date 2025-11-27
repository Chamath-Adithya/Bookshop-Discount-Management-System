package bookshop.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RoleSelectionController {
    @FXML
    private Label info_lbl;

    @FXML
    public void onAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AdminLogin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Login");
            stage.show();
        } catch (IOException e) {
            info_lbl.setText("Cannot open Admin login: " + e.getMessage());
            System.err.println("RoleSelection -> Admin load error: " + e.getMessage());
        }
    }

    @FXML
    public void onCashier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CashierLogin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cashier Login");
            stage.show();
        } catch (IOException e) {
            info_lbl.setText("Cannot open Cashier login: " + e.getMessage());
            System.err.println("RoleSelection -> Cashier load error: " + e.getMessage());
        }
    }
}
