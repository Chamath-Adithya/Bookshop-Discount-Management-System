package bookshop.controllers.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    @FXML private GridPane productsGrid;
    @FXML private TextField selectedProductField;
    @FXML private TextField productPriceField;
    @FXML private TextField discountInfoField;
    @FXML private TextField quantityField;
    @FXML private TextField customerPhoneField;
    @FXML private TextField customerTypeField;
    @FXML private Text discountPercentText;
    @FXML private VBox cartItemsBox;
    @FXML private Text subtotalText;
    @FXML private Text discountText;
    @FXML private Text totalText;
    @FXML private Button logoutBtn;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;
    @FXML private Button lookupCustomerBtn;

    private final List<Product> allProducts = new CopyOnWriteArrayList<>();
    private final List<Product> filteredProducts = new CopyOnWriteArrayList<>();
    private final Map<String, CartItem> cartItems = new LinkedHashMap<>();
    private ProductService productService;
    private CustomerService customerService;
    private Product selectedProduct;
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
        String phone = customerPhoneField.getText().trim();
        if (phone.isEmpty()) {
            showWarning("Please enter a phone number!");
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
        
        // Search for customer by phone
        Customer found = null;
        for (Customer c : customerService.getAllCustomers()) {
            if (c.getPhone().equals(phone)) {
                found = c;
                break;
            }
        }
        
        if (found != null) {
            currentCustomer = found;
            customerTypeField.setText(found.getType());
            
            // Show discount info
            if ("VIP".equalsIgnoreCase(found.getType())) {
                discountPercentText.setText("VIP Customer: 5% discount applied");
                discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: green; -fx-font-weight: bold;");
            } else {
                discountPercentText.setText("Regular Customer: No additional discount");
                discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: #666; -fx-font-weight: bold;");
            }
            showInfo("Customer found: " + found.getName());
        } else {
            currentCustomer = null;
            customerTypeField.setText("Not Found");
            discountPercentText.setText("No customer found with this phone number");
            discountPercentText.setStyle("-fx-font-size: 12; -fx-fill: red; -fx-font-weight: bold;");
            showWarning("Customer not found. Proceeding as Regular customer.");
        }
    }

    private void setupDateTime() {
        Thread dateTimeThread = new Thread(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (true) {
                try {
                    Platform.runLater(() -> {
                        dateTimeText.setText(LocalDateTime.now().format(formatter));
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
            if (lastMod == productsLastModified) {
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
        int row = 0;
        
        for (Product product : filteredProducts) {
            VBox productCard = createProductCard(product);
            productsGrid.add(productCard, 0, row);
            row++;
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; " +
                     "-fx-background-color: white; -fx-cursor: hand;");
        card.setPrefWidth(180);
        
        Text nameText = new Text(product.getName());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        Text priceText = new Text("Rs. " + String.format("%.2f", product.getRealPrice()));
        priceText.setStyle("-fx-font-size: 12; -fx-fill: green;");
        
        Text qtyText = new Text("Stock: " + product.getQuantity());
        qtyText.setStyle("-fx-font-size: 11; -fx-fill: gray;");
        
        String discountInfo = buildDiscountString(product);
        Text discountTextNode = new Text(discountInfo);
        discountTextNode.setStyle("-fx-font-size: 11; -fx-fill: #0F3D20;");
        
        Button selectBtn = new Button("Select");
        selectBtn.setPrefWidth(150);
        selectBtn.setStyle("-fx-padding: 8; -fx-background-color: #0F3D20; -fx-text-fill: white;");
        selectBtn.setOnAction(e -> handleProductSelect(product));
        
        card.getChildren().addAll(nameText, priceText, qtyText, discountTextNode, selectBtn);
        return card;
    }

    private String buildDiscountString(Product product) {
        Map<Integer, Double> discounts = product.getDiscountRules();
        if (discounts == null || discounts.isEmpty()) {
            return "No discount";
        }
        
        StringBuilder sb = new StringBuilder("Discounts: ");
        discounts.forEach((qty, price) -> 
            sb.append(qty).append("x: Rs").append(String.format("%.2f", price)).append("; ")
        );
        return sb.toString();
    }

    private void handleProductSelect(Product product) {
        selectedProduct = product;
        selectedProductField.setText(product.getName());
        productPriceField.setText(String.format("Rs. %.2f", product.getRealPrice()));
        
        String discountInfo = buildDiscountString(product);
        discountInfoField.setText(discountInfo);
        
        quantityField.clear();
        quantityField.requestFocus();
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

    @FXML
    private void handleAddToCart() {
        if (selectedProduct == null) {
            showWarning("Please select a product first!");
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (quantity <= 0) {
                showWarning("Quantity must be greater than 0!");
                return;
            }
            
            if (quantity > selectedProduct.getQuantity()) {
                showWarning("Insufficient stock! Available: " + selectedProduct.getQuantity());
                return;
            }
            
            CartItem item = new CartItem(selectedProduct, quantity, currentCustomer);
            
            if (cartItems.containsKey(selectedProduct.getProductId())) {
                CartItem existing = cartItems.get(selectedProduct.getProductId());
                existing.quantity += quantity;
            } else {
                cartItems.put(selectedProduct.getProductId(), item);
            }
            
            updateCartDisplay();
            quantityField.clear();
            showInfo("Added to cart!");
            
        } catch (NumberFormatException e) {
            showWarning("Please enter a valid quantity!");
        }
    }

    private void updateCartDisplay() {
        cartItemsBox.getChildren().clear();
        double subtotal = 0;
        double totalDiscount = 0;
        
        for (CartItem item : cartItems.values()) {
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
        box.setStyle("-fx-border-color: #eee; -fx-border-radius: 3; -fx-padding: 8; -fx-background-color: #f5f5f5;");
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox details = new VBox(3);
        Text nameText = new Text(item.product.getName());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        Text priceText = new Text(item.quantity + "x Rs. " + String.format("%.2f", item.product.getRealPrice()) + 
                                 " = Rs. " + String.format("%.2f", item.getSubtotal()));
        priceText.setStyle("-fx-font-size: 11;");
        
        details.getChildren().addAll(nameText, priceText);
        
        // Show discount details if applicable
        if (item.getTotalDiscount() > 0) {
            StringBuilder discountDetails = new StringBuilder();
            if (item.customer != null && "VIP".equalsIgnoreCase(item.customer.getType())) {
                discountDetails.append("(VIP Customer) ");
            }
            discountDetails.append("- Save Rs. ").append(String.format("%.2f", item.getTotalDiscount()));
            
            Text discText = new Text(discountDetails.toString());
            discText.setStyle("-fx-font-size: 10; -fx-fill: green;");
            details.getChildren().add(discText);
        }
        
        HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);
        
        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-padding: 5; -fx-font-size: 10; -fx-background-color: #cc0000; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item.product.getProductId());
            updateCartDisplay();
        });
        
        box.getChildren().addAll(details, removeBtn);
        return box;
    }

    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            showWarning("Cart is already empty!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will clear all items from the cart.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartItems.clear();
            updateCartDisplay();
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showWarning("Cart is empty! Add products before checkout.");
            return;
        }
        
        String totalStr = totalText.getText().replace("Rs. ", "").trim();
        double total = Double.parseDouble(totalStr);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Checkout");
        alert.setHeaderText("Total Amount: Rs. " + String.format("%.2f", total));
        alert.setContentText("Proceed with payment?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            generateBill();
            cartItems.clear();
            updateCartDisplay();
            showInfo("Payment successful! Thank you for your purchase.");
        }
    }

    private void generateBill() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String billName = "Bill_" + LocalDateTime.now().format(formatter) + ".txt";
        
        try {
            Files.createDirectories(Paths.get("bills"));
            
            StringBuilder bill = new StringBuilder();
            bill.append("===== BOOKSHOP BILL =====\n");
            bill.append("Date & Time: ").append(LocalDateTime.now()).append("\n");
            bill.append("----------------------------\n");
            
            for (CartItem item : cartItems.values()) {
                bill.append(item.product.getName()).append("\n");
                bill.append("  Qty: ").append(item.quantity).append(" x Rs. ")
                    .append(String.format("%.2f", item.product.getRealPrice())).append("\n");
                bill.append("  Subtotal: Rs. ").append(String.format("%.2f", item.getSubtotal())).append("\n");
                if (item.getTotalDiscount() > 0) {
                    bill.append("  Discount: - Rs. ").append(String.format("%.2f", item.getTotalDiscount())).append("\n");
                }
            }
            
            bill.append("----------------------------\n");
            bill.append("TOTAL: Rs. ").append(totalText.getText().replace("Rs. ", "")).append("\n");
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
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to logout?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.close();
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
