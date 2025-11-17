module bookshop {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow JavaFX to access your model classes if they are used in the UI
    opens bookshop.model to javafx.base;

    // Open the main package to JavaFX and testing
    opens bookshop;

    // Export the main package
    exports bookshop;
}
