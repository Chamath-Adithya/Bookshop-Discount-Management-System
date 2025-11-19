package bookshop.controllers.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class UserController {

    //<editor-fold desc="FXML Annotations - Left Pane">
    @FXML
    private Text timeText;

    @FXML
    private Text dateText;

    @FXML
    private Text greetingText;

    @FXML
    private Text userText;

    @FXML
    private Button logoutButton;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Center Pane">
    @FXML
    private ToggleButton itemToggle;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Right Pane (Cart)">
    @FXML
    private Button clearCartButton;

    @FXML
    private Button payButton;
    //</editor-fold>

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. It sets up the initial state of the UI.
     */
    @FXML
    public void initialize() {
        // Set current time and date dynamically
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        timeText.setText(LocalDateTime.now().format(timeFormatter));
        dateText.setText(LocalDateTime.now().format(dateFormatter));

        // Set a greeting based on the time of day
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) {
            greetingText.setText("Good Morning");
        } else if (hour < 18) {
            greetingText.setText("Good Afternoon");
        } else {
            greetingText.setText("Good Evening");
        }
    }

    // --- Event Handler Methods ---

    /**
     * Handles the action of clicking the logout button.
     */
    @FXML
    private void handleLogout() {
        System.out.println("Logout button clicked. Implement scene switching or application exit logic here.");
        // Example: System.exit(0);
    }

    /**
     * Handles the search action from the search button or by pressing Enter in the text field.
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        System.out.println("Search button clicked. Searching for: " + searchTerm);
        // Implement item search and display logic here.
    }

    /**
     * Handles adding an item to the cart when an item's VBox or "Add to Cart" button is clicked.
     */
    @FXML
    private void handleAddToCart(MouseEvent event) {
        System.out.println("Add to Cart action triggered on an item.");
        // Implement logic to identify the clicked item and add it to the cart data structure.
    }

    /**
     * Handles the action of clearing all items from the cart.
     */
    @FXML
    private void handleClearCart() {
        System.out.println("Clear Cart button clicked.");
        // Implement logic to clear all items from the cart view and data structure.
    }

    /**
     * Handles the payment process, including discount calculations.
     */
    @FXML
    private void handlePay() {
        System.out.println("Pay button clicked.");
        // Implement payment processing, discount calculation, and receipt generation logic here.
    }
}
