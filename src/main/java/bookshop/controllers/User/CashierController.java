package bookshop.controllers.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import bookshop.model.Customer;
import bookshop.model.Product;
import bookshop.service.CustomerService;
import bookshop.service.ProductService;

public class CashierController {

    @FXML private Text dateTimeText;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;
    @FXML private GridPane productsGrid;
    
    // Customer Section
    @FXML private TextField customerPhoneField;
    @FXML private Button lookupCustomerBtn;
    @FXML private Text customerTypeField;
    @FXML private Text discountPercentText;
    
    // Cart Section
    @FXML private VBox cartItemsBox;
    @FXML private Text subtotalText;
    @FXML private Text discountText;
    @FXML private Text totalText;
    @FXML private Button payButton;
    @FXML private Button logoutBtn;

    private final List<Product> allProducts = new CopyOnWriteArrayList<>();
    private final List<Product> filteredProducts = new CopyOnWriteArrayList<>();
    private final Map<String, CartItem> cartItems = new LinkedHashMap<>();
    
    private ProductService productService;
    private CustomerService customerService;
    private Customer currentCustomer;
    
    private WatchService watchService;
    private Thread fileWatchThread;
    private long productsLastModified = 0L;

    @FXML
    public void initialize() {
        System.out.println("[CashierController] Initializing...");
        setupDateTime();
        loadProducts();
        setupFileWatcher();
        setupEventHandlers();
        setupCustomerService();
        updateCartDisplay();
    }

    private void setupCustomerService() {
        try {
            customerService = new CustomerService();
        } catch (IOException e) {
            System.err.println("Failed to initialize customer service: " + e.getMessage());
        }
    }

    @FXML
    private void handleCustomerLookup() {
        String input = customerPhoneField.getText().trim();
        if (input.isEmpty()) {
            showWarning("Please enter a phone number or name!");
            return;
        }
        
        if (customerService == null) {
            try {
                customerService = new CustomerService();
            } catch (IOException e) {
                showError("Failed to load customer data: " + e.getMessage());
                return;
            }
        }
        
        // Search for customer by phone or name
        Customer found = null;
        for (Customer c : customerService.getAllCustomers()) {
            // Check phone match
            if (c.getPhone() != null && c.getPhone().equals(input)) {
                found = c;
                break;
            }
            // Check name match (case-insensitive)
            if (c.getName().equalsIgnoreCase(input)) {
                found = c;
                break;
            }
        }
        
        if (found != null) {
            currentCustomer = found;
            customerTypeField.setText(found.getType());
            
            // Show discount info
            if ("VIP".equalsIgnoreCase(found.getType())) {
                discountPercentText.setText("VIP: 5% OFF");
                discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: green; -fx-font-weight: bold;");
            } else {
                discountPercentText.setText("Regular");
                discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: #666;");
            }
            showInfo("Customer found: " + found.getName());
        } else {
            currentCustomer = null;
            customerTypeField.setText("Guest");
            discountPercentText.setText("No account found");
            discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: #999;");
            showWarning("Customer not found. Proceeding as Guest.");
        }
        
        // Recalculate cart totals as customer type might have changed
        updateCartDisplay();
    }

    private void setupDateTime() {
        Thread dateTimeThread = new Thread(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (true) {
                try {
                    Platform.runLater(() -> {
                        if (dateTimeText != null) {
                            dateTimeText.setText(LocalDateTime.now().format(formatter));
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "DateTimeThread");
        dateTimeThread.setDaemon(true);
        dateTimeThread.start();
    }

    private void setupFileWatcher() {
        fileWatchThread = new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                Path dataDir = Paths.get("data");
                if (Files.exists(dataDir)) {
                    dataDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    
                    while (true) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.context().toString().equals("products.csv")) {
                                try {
                                    Thread.sleep(300); // Debounce file change detection
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                                Platform.runLater(this::loadProducts);
                            }
                        }
                        if (!key.reset()) {
                            break;
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("File watcher error: " + e.getMessage());
            }
        }, "FileWatchThread");
        fileWatchThread.setDaemon(true);
        fileWatchThread.start();
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
        showInfo("Products refreshed successfully!");
    }

    private void loadProducts() {
        try {
            File prodFile = new File("data/products.csv");
            long lastMod = prodFile.exists() ? prodFile.lastModified() : 0L;
            
            // Only reload if file has changed
            if (lastMod == productsLastModified && !allProducts.isEmpty()) {
                return;
            }
            productsLastModified = lastMod;
            
            productService = new ProductService();
            allProducts.clear();
            allProducts.addAll(productService.getAllProducts());
            filteredProducts.clear();
            filteredProducts.addAll(allProducts);
            displayProducts();
            System.out.println("[CashierController] Loaded " + allProducts.size() + " products");
        } catch (IOException e) {
            showError("Failed to load products: " + e.getMessage());
        }
    }

    private void displayProducts() {
        productsGrid.getChildren().clear();
        int col = 0;
        int row = 0;
        int maxCols = 3; // Number of columns in grid
        
        for (Product product : filteredProducts) {
            VBox productCard = createProductCard(product);
            productsGrid.add(productCard, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);
        
        // Product Name
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        
        // Price and Stock
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("Rs. " + String.format("%.2f", product.getRealPrice()));
        priceLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label stockLabel = new Label("Stock: " + product.getQuantity());
        stockLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        priceBox.getChildren().addAll(priceLabel, spacer, stockLabel);
        
        // Discount Badge
        HBox badgeBox = new HBox();
        if (!product.getDiscountRules().isEmpty()) {
            Label badge = new Label("Bulk Offer Available");
            badge.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px;");
            badgeBox.getChildren().add(badge);
        }
        
        // Add to Cart Controls
        HBox controlsBox = new HBox(10);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        
        Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(1, product.getQuantity()), 1);
        qtySpinner.setEditable(true);
        qtySpinner.setPrefWidth(70);
        
        Button addBtn = new Button("Add");
        addBtn.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 15;");
        addBtn.setOnAction(e -> handleAddToCart(product, qtySpinner.getValue()));
        
        // Disable if out of stock
        if (product.getQuantity() <= 0) {
            qtySpinner.setDisable(true);
            addBtn.setDisable(true);
            addBtn.setText("Out of Stock");
        }
        
        controlsBox.getChildren().addAll(qtySpinner, addBtn);
        
        card.getChildren().addAll(nameLabel, priceBox, badgeBox, controlsBox);
        return card;
    }

    private void handleAddToCart(Product product, int quantity) {
        if (quantity <= 0) return;
        
        if (quantity > product.getQuantity()) {
            showWarning("Insufficient stock! Available: " + product.getQuantity());
            return;
        }
        
        CartItem item = new CartItem(product, quantity, currentCustomer);
        
        if (cartItems.containsKey(product.getProductId())) {
            CartItem existing = cartItems.get(product.getProductId());
            int newQty = existing.quantity + quantity;
            if (newQty > product.getQuantity()) {
                showWarning("Cannot add more than available stock!");
                return;
            }
            existing.quantity = newQty;
            // Update customer ref in case it changed
            existing.customer = currentCustomer; 
        } else {
            cartItems.put(product.getProductId(), item);
        }
        
        updateCartDisplay();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        filteredProducts.clear();
        
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.getName().toLowerCase().contains(query) || 
                    p.getProductId().toLowerCase().contains(query)) {
                    filteredProducts.add(p);
                }
            }
        }
        displayProducts();
    }

    private void updateCartDisplay() {
        cartItemsBox.getChildren().clear();
        double subtotal = 0;
        double totalDiscount = 0;
        
        for (CartItem item : cartItems.values()) {
            // Update customer reference for all items to ensure current VIP status applies
            item.customer = currentCustomer;
            
            HBox itemBox = createCartItemBox(item);
            cartItemsBox.getChildren().add(itemBox);
            
            subtotal += item.getSubtotal();
            totalDiscount += item.getTotalDiscount();
        }
        
        double total = subtotal - totalDiscount;
        
        subtotalText.setText("Rs. " + String.format("%.2f", subtotal));
        discountText.setText("- Rs. " + String.format("%.2f", totalDiscount));
        totalText.setText("Rs. " + String.format("%.2f", Math.max(0, total)));
    }

    private HBox createCartItemBox(CartItem item) {
        HBox box = new HBox(10);
        box.setStyle("-fx-border-color: #eee; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: white;");
        box.setAlignment(Pos.CENTER_LEFT);
        
        VBox details = new VBox(3);
        Text nameText = new Text(item.product.getName());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        Text priceText = new Text(item.quantity + " x Rs. " + String.format("%.2f", item.product.getRealPrice()));
        priceText.setStyle("-fx-font-size: 12px; -fx-fill: #666;");
        
        details.getChildren().addAll(nameText, priceText);
        
        // Show discount details if applicable
        if (item.getTotalDiscount() > 0) {
            Text discText = new Text("Saved: Rs. " + String.format("%.2f", item.getTotalDiscount()));
            discText.setStyle("-fx-font-size: 11px; -fx-fill: #4CAF50; -fx-font-weight: bold;");
            details.getChildren().add(discText);
        }
        
        HBox.setHgrow(details, Priority.ALWAYS);
        
        VBox actions = new VBox(5);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Text totalItemPrice = new Text("Rs. " + String.format("%.2f", item.getSubtotal() - item.getTotalDiscount()));
        totalItemPrice.setStyle("-fx-font-weight: bold;");
        
        Button removeBtn = new Button("×");
        removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff6b6b; -fx-font-size: 16px; -fx-padding: 0; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item.product.getProductId());
            updateCartDisplay();
        });
        
        actions.getChildren().addAll(totalItemPrice, removeBtn);
        
        box.getChildren().addAll(details, actions);
        return box;
    }

    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText(null);
        alert.setContentText("Clear all items from cart?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartItems.clear();
            updateCartDisplay();
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showWarning("Cart is empty!");
            return;
        }
        
        String totalStr = totalText.getText().replace("Rs. ", "").trim();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Checkout");
        alert.setHeaderText("Total Amount: " + totalStr);
        alert.setContentText("Proceed with payment?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Deduct stock
            try {
                if (productService == null) {
                    productService = new ProductService();
                }
                
                for (CartItem item : cartItems.values()) {
                    Product p = item.product;
                    int newQty = p.getQuantity() - item.quantity;
                    if (newQty < 0) {
                        showError("Insufficient stock for " + p.getName() + ". Transaction cancelled.");
                        return; // Cancel transaction
                    }
                    p.setQuantity(newQty);
                    productService.updateProduct(p);
                }
                productService.saveAllProducts();
                
                generateBill();
                cartItems.clear();
                updateCartDisplay();
                showInfo("Payment successful! Stock updated.");
                
            } catch (IOException e) {
                showError("Failed to update stock: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void generateBill() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String billName = "Bill_" + LocalDateTime.now().format(formatter) + ".txt";
        
        try {
            Files.createDirectories(Paths.get("bills"));
            
            StringBuilder bill = new StringBuilder();
            bill.append("===== BOOKSHOP BILL =====\n");
            bill.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            if (currentCustomer != null) {
                bill.append("Customer: ").append(currentCustomer.getName())
                    .append(" (").append(currentCustomer.getType()).append(")\n");
            } else {
                bill.append("Customer: Guest\n");
            }
            bill.append("----------------------------\n");
            
            for (CartItem item : cartItems.values()) {
                bill.append(String.format("%-20s x%d\n", item.product.getName(), item.quantity));
                bill.append(String.format("  @ Rs. %.2f\n", item.product.getRealPrice()));
                if (item.getTotalDiscount() > 0) {
                    bill.append(String.format("  Discount: -Rs. %.2f\n", item.getTotalDiscount()));
                }
                bill.append(String.format("  Total:    Rs. %.2f\n", item.getSubtotal() - item.getTotalDiscount()));
            }
            
            bill.append("----------------------------\n");
            bill.append("GRAND TOTAL: ").append(totalText.getText()).append("\n");
            bill.append("===========================\n");
            
            Files.write(Paths.get("bills", billName), bill.toString().getBytes());
            System.out.println("Bill saved: bills/" + billName);
            
        } catch (IOException e) {
            System.err.println("Failed to save bill: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupEventHandlers() {
        searchField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleSearch();
            }
        });
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for cart items
    private static class CartItem {
        Product product;
        int quantity;
        Customer customer;
        
        CartItem(Product product, int quantity, Customer customer) {
            this.product = product;
            this.quantity = quantity;
            this.customer = customer;
        }
        
        double getSubtotal() {
            return product.getRealPrice() * quantity;
        }
        
        double getTotalDiscount() {
            double discount = 0;
            
            // Customer tier discount (VIP gets 5%)
            if (customer != null && "VIP".equalsIgnoreCase(customer.getType())) {
                discount += getSubtotal() * 0.05;
            }
            
            // Product bulk discount based on quantity
            Map<Integer, Double> discounts = product.getDiscountRules();
            if (discounts != null && !discounts.isEmpty()) {
                // Find applicable discount (highest quantity threshold <= current quantity)
                Integer bestQty = null;
                for (Integer qty : discounts.keySet()) {
                    if (qty <= quantity) {
                        if (bestQty == null || qty > bestQty) {
                            bestQty = qty;
                        }
                    }
                }
                
                if (bestQty != null) {
                    double discountPrice = discounts.get(bestQty);
                    double discountAmount = (product.getRealPrice() - discountPrice) * quantity;
                    discount += discountAmount;
                }
            }
            
            return discount;
        }
    }
}
