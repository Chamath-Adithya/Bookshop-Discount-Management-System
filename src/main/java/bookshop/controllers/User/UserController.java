package bookshop.controllers.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bookshop.model.Product;
import bookshop.service.ProductService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * UserController manages the cashier/POS interface.
 * Handles product selection, shopping cart management, and billing.
 */
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

    @FXML
    private javafx.scene.layout.VBox cartItemsContainer;

    @FXML
    private Text totalAmountText;
    //</editor-fold>

    // Business Logic Components
    private ProductService productService;
    private Map<String, CartItem> shoppingCart;
    private double totalAmount = 0.0;
    // File watcher for product updates
    private ScheduledExecutorService productsWatcher;
    private long productsLastModified = 0L;

    /**
     * Inner class to represent an item in the shopping cart.
     */
    private static class CartItem {
        Product product;
        int quantity;
        double subtotal;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
            this.subtotal = product.getRealPrice() * quantity;
        }

        void updateQuantity(int newQuantity) {
            this.quantity = newQuantity;
            this.subtotal = product.getRealPrice() * quantity;
        }
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up the initial state of the UI.
     */
    @FXML
    public void initialize() {
        System.out.println("[UserController] Initializing POS Interface...");
        
        // Initialize shopping cart
        this.shoppingCart = new HashMap<>();
        
        // Initialize product service
        try {
            this.productService = new ProductService();
            System.out.println("[UserController] ProductService loaded successfully");
        } catch (IOException e) {
            System.err.println("[UserController] Error loading ProductService: " + e.getMessage());
            showAlert("Error", "Failed to load product data: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Set current time and date dynamically
        updateTimeAndDate();

        // Set a greeting based on the time of day
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) {
            greetingText.setText("Good Morning");
        } else if (hour < 18) {
            greetingText.setText("Good Afternoon");
        } else {
            greetingText.setText("Good Evening");
        }
        
        // Load and display products from database
        loadProductsToGrid();

        // Start a lightweight file-watcher to auto-refresh products when admin updates the CSV.
        try {
            File prodFile = new File("data/products.csv");
            productsLastModified = prodFile.exists() ? prodFile.lastModified() : 0L;
            productsWatcher = Executors.newSingleThreadScheduledExecutor();
            productsWatcher.scheduleAtFixedRate(() -> {
                try {
                    long lm = prodFile.exists() ? prodFile.lastModified() : 0L;
                    if (lm != productsLastModified) {
                        // Load fresh ProductService in background thread then update UI
                        try {
                            ProductService fresh = new ProductService();
                            productsLastModified = lm;
                            Platform.runLater(() -> {
                                productService = fresh;
                                loadProductsToGrid();
                                System.out.println("[UserController] Detected products.csv change, reloaded products.");
                            });
                        } catch (IOException ioe) {
                            System.err.println("[UserController] Failed to reload products: " + ioe.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("[UserController] Error in products watcher: " + ex.getMessage());
                }
            }, 3, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("[UserController] Product watcher failed to start: " + e.getMessage());
        }
    }

    /**
     * Updates the time and date displayed on the left panel.
     */
    private void updateTimeAndDate() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDateTime now = LocalDateTime.now();
        timeText.setText(now.format(timeFormatter));
        dateText.setText(now.format(dateFormatter));
    }

    // --- Event Handler Methods ---

    /**
    /**
     * Handles the action of clicking the logout button.
     * Returns to the role selection screen.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handleLogout(ActionEvent event) {
        System.out.println("[UserController] Logout button clicked");
        try {
            // shutdown background watcher to avoid threads remaining after navigation
            if (productsWatcher != null && !productsWatcher.isShutdown()) {
                productsWatcher.shutdownNow();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/RoleSelection.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Select Role");
            stage.show();
        } catch (IOException e) {
            System.err.println("[UserController] Error during logout: " + e.getMessage());
            showAlert("Error", "Failed to return to role selection: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the search action from the search button or by pressing Enter in the text field.
     * Filters products based on the search term.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        System.out.println("[UserController] Search button clicked. Searching for: " + searchTerm);

        // Filter products based on search term
        loadProductsToGrid(searchTerm);
    }

    /**
     * Handles adding an item to the cart when "Add to Cart" button is clicked.
     */
    @FXML
    private void handleAddToCart(ActionEvent event) {
        addToCartByNode((Node) event.getSource());
    }

    @FXML
    private void handleAddToCart(MouseEvent event) {
        addToCartByNode((Node) event.getSource());
    }

    /**
     * Internal helper to find product card VBox from the event source and add to cart.
     */
    private void addToCartByNode(Node source) {
        System.out.println("[UserController] Add to Cart triggered from node: " + source);
        try {
            // Walk up the parent chain to find the product card VBox
            Node parent = source;
            while (parent != null && !(parent instanceof VBox)) {
                parent = parent.getParent();
            }

            if (parent instanceof VBox) {
                VBox productCard = (VBox) parent;
                Object userData = productCard.getUserData();
                if (userData instanceof String) {
                    String productId = (String) userData;
                    Product product = productService.findProductById(productId);
                    if (product != null) {
                        addProductToCart(product, 1);
                        System.out.println("[UserController] Product added to cart: " + product.getName());
                        showAlert("Success", product.getName() + " added to cart!", Alert.AlertType.INFORMATION);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error adding to cart: " + e.getMessage());
            showAlert("Error", "Failed to add product to cart: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Loads all products from ProductService and displays them in the products grid.
     * Can optionally filter by a search term.
     */
    private void loadProductsToGrid() {
        loadProductsToGrid(null);
    }

    private void loadProductsToGrid(String searchTerm) {
        try {
            if (productService == null) {
                productService = new ProductService();
            }
            
            List<Product> allProducts = productService.getAllProducts();
            System.out.println("[UserController] Found " + allProducts.size() + " products to display");
            
            // Navigate through the scene to find the ScrollPane
            for (javafx.scene.Node node : ((javafx.scene.layout.VBox) searchButton.getParent().getParent()).getChildren()) {
                if (node instanceof javafx.scene.control.ScrollPane) {
                    javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) node;
                    
                    if (scrollPane.getContent() instanceof javafx.scene.layout.GridPane) {
                        javafx.scene.layout.GridPane grid = (javafx.scene.layout.GridPane) scrollPane.getContent();
                        grid.getChildren().clear();
                        
                        int colIndex = 0;
                        int rowIndex = 0;
                        for (Product product : allProducts) {
                            // Filter logic
                            if (searchTerm != null && !searchTerm.isEmpty()) {
                                String lowerTerm = searchTerm.toLowerCase();
                                boolean matches = product.getName().toLowerCase().contains(lowerTerm) || 
                                                  product.getProductId().toLowerCase().contains(lowerTerm);
                                if (!matches) {
                                    continue;
                                }
                            }
                            // Create product card
                            javafx.scene.layout.VBox productCard = new javafx.scene.layout.VBox();
                            productCard.setAlignment(javafx.geometry.Pos.CENTER);
                            productCard.setSpacing(10.0);
                            productCard.setStyle("-fx-background-color: #f4f4f4; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                            productCard.setPadding(new javafx.geometry.Insets(15.0));
                            productCard.setUserData(product.getProductId()); // Store product ID
                            
                            // Image placeholder
                            Text productImage = new Text("Item");
                            productImage.setStyle("-fx-font-size: 40;");
                            
                            // Product name
                            Text nameText = new Text(product.getName());
                            nameText.setStyle("-fx-fill: black; -fx-font-size: 18; -fx-font-weight: bold;");
                            
                            // Product price
                            Text priceText = new Text(String.format("Rs. %.2f", product.getRealPrice()));
                            priceText.setStyle("-fx-fill: green; -fx-font-size: 14; -fx-font-weight: bold;");
                            
                            // Add to Cart button
                            Button addBtn = new Button("Add to Cart");
                            addBtn.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-background-color: #0F3D20; -fx-text-fill: white;");
                            addBtn.setOnAction(this::handleAddToCart);
                            
                            productCard.getChildren().addAll(productImage, nameText, priceText, addBtn);
                            
                            // Add to grid
                            grid.add(productCard, colIndex, rowIndex);
                            colIndex++;
                            if (colIndex >= 2) { // 2 columns per row
                                colIndex = 0;
                                rowIndex++;
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error loading products to grid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of clearing all items from the cart.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handleClearCart(ActionEvent event) {
        System.out.println("[UserController] Clear Cart button clicked");
        
        if (shoppingCart.isEmpty()) {
            showAlert("Info", "Cart is already empty", Alert.AlertType.INFORMATION);
            return;
        }

        shoppingCart.clear();
        totalAmount = 0.0;
        updateCartDisplay();
        System.out.println("[UserController] Shopping cart cleared");
    }

    /**
     * Handles the payment process, including discount calculations and receipt generation.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handlePay(ActionEvent event) {
        System.out.println("[UserController] Pay button clicked");

        if (shoppingCart.isEmpty()) {
            showAlert("Warning", "Shopping cart is empty. Please add items before paying.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Calculate final amount with any applicable discounts
            double finalAmount = calculateFinalAmount();
            
            // TODO: Process payment (integrate with payment gateway if needed)
            // For now, just show receipt
            showReceipt(finalAmount);
            
            // Clear cart after successful payment
            shoppingCart.clear();
            totalAmount = 0.0;
            updateCartDisplay();
            
            System.out.println("[UserController] Payment processed successfully. Amount: Rs. " + finalAmount);
        } catch (Exception e) {
            System.err.println("[UserController] Error during payment: " + e.getMessage());
            showAlert("Error", "Payment failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Refresh the product grid by reloading products from disk.
     */
    @FXML
    @SuppressWarnings("unused")
    private void handleRefreshProducts(ActionEvent event) {
        System.out.println("[UserController] Refresh products requested");
        loadProductsToGrid();
    }

    // --- Business Logic Methods ---

    /**
     * Adds a product to the shopping cart or increases its quantity if already present.
     */
    private void addProductToCart(Product product, int quantity) {
        String productId = product.getProductId();
        
        if (shoppingCart.containsKey(productId)) {
            // Product already in cart, increase quantity
            CartItem item = shoppingCart.get(productId);
            item.updateQuantity(item.quantity + quantity);
            System.out.println("[UserController] Updated quantity for " + product.getName() + ": " + item.quantity);
        } else {
            // Add new product to cart
            CartItem item = new CartItem(product, quantity);
            shoppingCart.put(productId, item);
            System.out.println("[UserController] Added new product to cart: " + product.getName());
        }
        
        updateCartDisplay();
    }

    /**
     * Updates the total amount and cart display.
     */
    private void updateCartDisplay() {
        // Recalculate total amount
        totalAmount = 0.0;
        for (CartItem item : shoppingCart.values()) {
            totalAmount += item.subtotal;
        }

        System.out.println("[UserController] Cart updated. Total items: " + shoppingCart.size() + 
                         ", Total amount: Rs. " + String.format("%.2f", totalAmount));
        
        // Update the UI with cart items
        if (cartItemsContainer != null) {
            cartItemsContainer.getChildren().clear();
            for (CartItem item : shoppingCart.values()) {
                javafx.scene.layout.VBox itemBox = new javafx.scene.layout.VBox();
                itemBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-border-width: 0 0 1 0; -fx-padding: 5;");
                itemBox.setSpacing(5.0);

                // Item name and price
                javafx.scene.layout.HBox nameBox = new javafx.scene.layout.HBox();
                nameBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                Text nameText = new Text(item.product.getName());
                nameText.setStyle("-fx-fill: #0a0a0a; -fx-font-weight: bold; -fx-font-size: 16;");
                Text priceText = new Text(String.format("%.2f", item.product.getRealPrice()));
                priceText.setStyle("-fx-fill: #0a0a0a; -fx-font-size: 16;");
                priceText.setWrappingWidth(80.0);
                javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                nameBox.getChildren().addAll(nameText, spacer, priceText);

                // Quantity controls
                javafx.scene.layout.HBox qtyBox = new javafx.scene.layout.HBox();
                qtyBox.setAlignment(javafx.geometry.Pos.CENTER);
                qtyBox.setSpacing(5.0);
                Button minusBtn = new Button("-");
                minusBtn.setPrefWidth(30.0);
                minusBtn.setStyle("-fx-padding: 2;");
                TextField qtyField = new TextField(String.valueOf(item.quantity));
                qtyField.setPrefWidth(40.0);
                qtyField.setAlignment(javafx.geometry.Pos.CENTER);
                qtyField.setEditable(false);
                Button plusBtn = new Button("+");
                plusBtn.setPrefWidth(30.0);
                plusBtn.setStyle("-fx-padding: 2;");

                minusBtn.setOnAction(e -> {
                    if (item.quantity > 1) {
                        item.quantity--;
                        qtyField.setText(String.valueOf(item.quantity));
                        item.subtotal = item.product.getRealPrice() * item.quantity;
                        updateCartDisplay();
                    }
                });
                plusBtn.setOnAction(e -> {
                    item.quantity++;
                    qtyField.setText(String.valueOf(item.quantity));
                    item.subtotal = item.product.getRealPrice() * item.quantity;
                    updateCartDisplay();
                });

                qtyBox.getChildren().addAll(minusBtn, qtyField, plusBtn);
                itemBox.getChildren().addAll(nameBox, qtyBox);
                cartItemsContainer.getChildren().add(itemBox);
            }
        }

        // Update total amount text
        if (totalAmountText != null) {
            totalAmountText.setText(String.format("Rs. %.2f", totalAmount));
        }
    }

    /**
     * Calculates the final amount including any discounts.
     */
    private double calculateFinalAmount() {
        // For now, just return the total amount
        // TODO: Apply discount codes, VIP customer discounts, bulk discounts, etc.
        return totalAmount;
    }

    /**
     * Shows a receipt for the completed transaction.
     */
    private void showReceipt(double finalAmount) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("========== RECEIPT ==========\n");
        receipt.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        receipt.append("----------\n");
        
        for (CartItem item : shoppingCart.values()) {
            receipt.append(item.product.getName())
                   .append(" x").append(item.quantity)
                   .append(" @ Rs. ").append(String.format("%.2f", item.product.getRealPrice()))
                   .append(" = Rs. ").append(String.format("%.2f", item.subtotal))
                   .append("\n");
        }
        
        receipt.append("----------\n");
        receipt.append("Total: Rs. ").append(String.format("%.2f", finalAmount)).append("\n");
        receipt.append("=============================\n");
        receipt.append("Thank you for shopping!\n");

        System.out.println("[UserController] Receipt:\n" + receipt.toString());
        showAlert("Receipt", receipt.toString(), Alert.AlertType.INFORMATION);
    }

    /**
     * Utility method to show alerts.
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
