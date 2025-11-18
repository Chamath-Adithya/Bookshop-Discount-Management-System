module bookshop {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow JavaFX to access your model classes if they are used in the UI
    opens bookshop.model to javafx.base;

    // This opens the main package (for the App class)
    opens bookshop to javafx.fxml;

    // Add this line to allow FXML to access your LoginController
    opens bookshop.controllers to javafx.fxml;
    opens bookshop.controllers.User to javafx.fxml;
    opens bookshop.controllers.Admin to javafx.fxml;

    // Export the main package
    exports bookshop;
}
