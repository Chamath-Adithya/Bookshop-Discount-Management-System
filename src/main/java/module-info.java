module bookshop {
    requires javafx.controls;
    requires javafx.fxml;

    // Allow JavaFX to access your model classes if they are used in the UI
    opens bookshop.model to javafx.base;

    // Open controllers to JavaFX FXML loader
    opens bookshop.controllers to javafx.fxml;
    opens bookshop.controllers.Admin to javafx.fxml;
    opens bookshop.controllers.User to javafx.fxml;

    // Open the main package to JavaFX and testing
    opens bookshop;
    
    // Open service package for testing
    opens bookshop.service;

    // Export the main package
    exports bookshop;
    exports bookshop.service;
}
