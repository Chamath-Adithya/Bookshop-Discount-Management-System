package bookshop.controllers.Admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bookshop.model.Customer;
import bookshop.model.Product;
import bookshop.model.RegularCustomer;
import bookshop.model.VIPCustomer;
import bookshop.service.CustomerService;
import bookshop.service.DiscountService;
import bookshop.service.ProductService;
import bookshop.util.FileHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Map;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * AdminController manages the admin dashboard UI and admin operations.
 * Handles product management, discount management, user management, customer viewing, and reports.
 */
public class AdminController {

    //<editor-fold desc="FXML Annotations - Header">
    @FXML
    private Text adminNameText;

    @FXML
    private Button logoutButton;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Sidebar Menu">
    @FXML
    private Button productsBtn;



    @FXML
    private Button customersBtn;

    @FXML
    private Button usersBtn;


    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Content Tab Pane">
    @FXML
    private TabPane contentTabPane;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Products Tab">
    @FXML
    private TextField productIdField;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productQtyField;

    @FXML
    private Button addProductBtn;

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, String> productIdCol;

    @FXML
    private TableColumn<Product, String> productNameCol;

    @FXML
    private TableColumn<Product, Double> productPriceCol;

    @FXML
    private TableColumn<Product, String> productQtyCol;

    @FXML
    private TableColumn<Product, String> productDiscountCol;

    @FXML
    private TableColumn<Product, String> productActionCol;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Discounts Tab">
    @FXML
    private TextField discountCodeField;

    @FXML
    private TextField discountTypeField;

    @FXML
    private TextField discountValueField;

    //<editor-fold desc="FXML Annotations - Customers Tab">
    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, String> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, String> customerTypeCol;

    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    @FXML
    private TableColumn<Customer, String> customerActionCol;
    
    @FXML
    private TextField customerNameField;
    
    @FXML
    private ComboBox<String> customerTypeField;
    
    @FXML
    private TextField customerPhoneField;
    
    @FXML
    private Button addCustomerBtn;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Users Tab">
    @FXML
    private TextField userUsernameField;

    @FXML
    private PasswordField userPasswordField;

    @FXML
    private ComboBox<String> userRoleField;

    @FXML
    private Button addUserBtn;

    @FXML
    private TableView<?> usersTable;

    @FXML
    private TableColumn<?, ?> userIdCol;

    @FXML
    private TableColumn<?, ?> userUsernameCol;

    @FXML
    private TableColumn<?, ?> userRoleCol;

    @FXML
    private TableColumn<?, ?> userActionCol;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Reports Tab">
    @FXML
    private Button salesReportBtn;


    //</editor-fold>

    /**
     * Initializes the admin controller after FXML has been loaded.
     * Sets up initial UI state and loads data if needed.
     */
    @FXML
    public void initialize() {
        System.out.println("[AdminController] Initializing Admin Dashboard...");
        adminNameText.setText("Admin User");
        
        // Initialize services
        try {
            this.productService = new ProductService();
            this.discountService = new DiscountService();
            this.customerService = new CustomerService();
            System.out.println("[AdminController] Services initialized");
        } catch (IOException e) {
            System.err.println("[AdminController] Error initializing services: " + e.getMessage());
        }

        // Make ID fields read-only/disabled as they are auto-generated or for display only
        productIdField.setEditable(false);
        productIdField.setPromptText("Auto-generated");
        
        // Load initial data for tables
        // Setup product row selection to allow editing
        productsTable.setOnMouseClicked(e -> {
            Product selected = productsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                productIdField.setText(selected.getProductId());
                productNameField.setText(selected.getName());
                productPriceField.setText(String.valueOf(selected.getRealPrice()));
                productQtyField.setText(String.valueOf(selected.getQuantity()));
                addProductBtn.setText("Update Product");
                editingProduct = true;
                editingProductId = selected.getProductId();
            }
        });
        loadProductsData();

        loadCustomersData();
        loadUsersData();
        
        // Initialize role and customer type combo boxes
        userRoleField.setItems(FXCollections.observableArrayList("MANAGER", "CASHIER", "ADMIN"));
        customerTypeField.setItems(FXCollections.observableArrayList("REGULAR", "VIP"));
    }

    // --- User edit support ---
    private boolean editingUser = false;
    private String editingUserId = null;
    
    // --- Customer edit support ---
    private boolean editingCustomer = false;
    private String editingCustomerId = null;

    // --- Menu Button Handlers ---

    /**
     * Handles clicking on the Products menu button.
     */
    @FXML
    private void handleProductsMenu(ActionEvent event) {
        System.out.println("[AdminController] Products menu clicked");
        contentTabPane.getSelectionModel().selectFirst(); // Select Products tab
    }



    /**
     * Handles clicking on the Customers menu button.
     */
    @FXML
    private void handleCustomersMenu(ActionEvent event) {
        System.out.println("[AdminController] Customers menu clicked");
        contentTabPane.getSelectionModel().select(1); // Select Customers tab
    }

    /**
     * Handles clicking on the Users menu button.
     */
    @FXML
    private void handleUsersMenu(ActionEvent event) {
        System.out.println("[AdminController] Users menu clicked");
        contentTabPane.getSelectionModel().select(2); // Select Users tab
    }



    // --- Products Tab Handlers ---

    /**
     * Handles adding a new product.
     */
    @FXML
    private void handleAddProduct(ActionEvent event) {
        String productId = productIdField.getText().trim();
        String productName = productNameField.getText().trim();
        String productPrice = productPriceField.getText().trim();
        String productQty = productQtyField.getText().trim();

        System.out.println("[AdminController] Add/Update Product: ID=" + productId + ", Name=" + productName 
                           + ", Price=" + productPrice + ", Qty=" + productQty);

        if (productName.isEmpty() || productPrice.isEmpty()) {
            System.out.println("[AdminController] Product name or price is empty!");
            showError("Product Name and Price are required.");
            return;
        }

        try {
            double price = Double.parseDouble(productPrice);
            int qty = 0;
            if (!productQty.isEmpty()) {
                try {
                    qty = Integer.parseInt(productQty);
                } catch (NumberFormatException nfe) {
                    qty = 0;
                }
            }

            if (productService == null) {
                productService = new ProductService();
            }

            if (editingProduct && editingProductId != null) {
                // Update existing product
                Product existing = productService.findProductById(editingProductId);
                if (existing != null) {
                    existing.setName(productName);
                    existing.setRealPrice(price);
                    existing.setQuantity(qty);
                    // Persist changes
                    productService.saveAllProducts();
                    System.out.println("[AdminController] Product updated successfully!");
                    showInfo("Product updated successfully!");
                } else {
                    System.err.println("[AdminController] Product to update not found: " + editingProductId);
                    showError("Product not found for update.");
                }
            } else {
                // Add new product - ID will be auto-generated by service
                Product p = new Product(null, productName, price);
                p.setQuantity(qty);
                productService.addProduct(p);
                System.out.println("[AdminController] Product added successfully!");
                showInfo("Product added successfully! ID: " + p.getProductId());
            }
        } catch (NumberFormatException nfe) {
            System.err.println("[AdminController] Invalid price format: " + nfe.getMessage());
            showError("Invalid price format.");
            return;
        } catch (IOException ioe) {
            System.err.println("[AdminController] Failed to add/update product: " + ioe.getMessage());
            showError("Failed to save product: " + ioe.getMessage());
            return;
        }

        // Clear fields and reset editing state
        productIdField.clear();
        productNameField.clear();
        productPriceField.clear();
        productQtyField.clear();
        editingProduct = false;
        editingProductId = null;
        addProductBtn.setText("Add Product");

        // Reload table
        loadProductsData();
    }


        



    // --- Users Tab Handlers ---

    /**
     * Handles adding/updating a user.
     */
    @FXML
    private void handleAddUser(ActionEvent event) {
        String username = userUsernameField.getText().trim();
        String password = userPasswordField.getText().trim();
        String role = userRoleField.getValue();

        if (role == null) role = "";
        role = role.trim();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showError("Username, Password, and Role are required.");
            return;
        }

        try {
            if (editingUser && editingUserId != null) {
                // Update existing user in CSV
                List<String> lines = FileHandler.readCsv("data/users.csv");
                List<String> out = new ArrayList<>();
                for (String line : lines) {
                    String t = line.trim();
                    if (t.isEmpty() || t.startsWith("#")) { out.add(line); continue; }
                    String[] cols = line.split(",");
                    if (cols.length >= 1 && cols[0].trim().equals(editingUserId)) {
                        out.add(String.format("%s,%s,%s,%s", editingUserId, username, password, role));
                    } else {
                        out.add(line);
                    }
                }
                FileHandler.writeCsv("data/users.csv", out);
                showInfo("User updated successfully!");
                editingUser = false;
                editingUserId = null;
                addUserBtn.setText("Add User");
            } else {
                // Generate new user ID
                String newUserId = generateNextUserId();
                String line = String.format("%s,%s,%s,%s", newUserId, username, password, role);
                FileHandler.appendLine("data/users.csv", line);
                showInfo("User added successfully! ID: " + newUserId);
            }
        } catch (IOException ioe) {
            showError("Failed to add/update user: " + ioe.getMessage());
        }

        // Clear fields and reset editing state
        userUsernameField.clear();
        userPasswordField.clear();
        userRoleField.setValue(null);
        editingUser = false;
        editingUserId = null;
        addUserBtn.setText("Add User");

        // Reload table
        loadUsersData();
    }

    /**
     * Generates the next user ID by finding the highest existing ID and incrementing.
     */
    private String generateNextUserId() throws IOException {
        List<String> lines = FileHandler.readCsv("data/users.csv");
        int maxId = 0;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] cols = line.split(",");
            if (cols.length >= 1) {
                String id = cols[0].trim();
                // Extract numeric part from ID like "u01", "u02", etc.
                if (id.startsWith("u")) {
                    try {
                        int num = Integer.parseInt(id.substring(1));
                        if (num > maxId) maxId = num;
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
        }
        
        return String.format("u%02d", maxId + 1);
    }



    // --- Logout Handler ---

    /**
     * Handles the logout action, returning to role selection screen.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("[AdminController] Logout button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/RoleSelection.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Select Role");
            stage.show();
        } catch (IOException e) {
            System.err.println("[AdminController] Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Data Loading Methods (Placeholder) ---

    /**
     * Loads products data from CSV into the products table.
     */
    /**
     * Loads products data from CSV into the products table.
     */
    private void loadProductsData() {
        System.out.println("[AdminController] Loading products data...");
        try {
            if (productService == null) {
                productService = new ProductService();
            }
            List<Product> list = productService.getAllProducts();
            ObservableList<Product> obs = FXCollections.observableArrayList(list);
            
            // Setup columns
            productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
            productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            productPriceCol.setCellValueFactory(new PropertyValueFactory<>("realPrice"));
            productQtyCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantity())));
            
            // Active Discount Column
            productDiscountCol.setCellValueFactory(cell -> {
                Map<Integer, Double> rules = cell.getValue().getDiscountRules();
                if (rules.isEmpty()) return new SimpleStringProperty("-");
                if (rules.size() == 1) {
                    // Display single rule
                    Map.Entry<Integer, Double> rule = rules.entrySet().iterator().next();
                    return new SimpleStringProperty("Buy " + rule.getKey() + "+ @ " + String.format("%.2f", rule.getValue()));
                } else {
                    // Display count of rules
                    return new SimpleStringProperty(rules.size() + " rules");
                }
            });

            productsTable.setItems(obs);

            // Add action buttons (Discount / Delete) to action column
            productActionCol.setCellFactory(col -> new TableCell<Product, String>() {
                private final Button discountBtn = new Button("Discount");
                private final Button delBtn = new Button("Delete");
                private final HBox box = new HBox(5, discountBtn, delBtn);

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        Product p = getTableView().getItems().get(idx);
                        
                        // Check if discount exists
                        boolean hasDiscount = !p.getDiscountRules().isEmpty();
                        
                        // Style buttons
                        if (hasDiscount) {
                            discountBtn.setText("Edit Deal");
                            discountBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 10px;");
                        } else {
                            discountBtn.setText("Add Deal");
                            discountBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px;");
                        }
                        delBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 10px;");

                        // Discount Button Action - Custom Dialog
                        discountBtn.setOnAction(ev -> showDiscountDialog(p));

                        // Delete Button Action
                        delBtn.setOnAction(ev -> {
                            try {
                                if (productService == null) productService = new ProductService();
                                productService.deleteProduct(p.getProductId());
                                loadProductsData();
                                System.out.println("[AdminController] Product deleted: " + p.getProductId());
                            } catch (Exception ex) {
                                System.err.println("[AdminController] Failed to delete product: " + ex.getMessage());
                                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to delete product: " + ex.getMessage());
                                a.showAndWait();
                            }
                        });
                        
                        setGraphic(box);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading products: " + e.getMessage());
        }
    }

    /**
     * Shows a custom dialog to manage discounts for a product.
     * Supports multiple discount tiers.
     */
    /**
     * Shows a custom dialog to manage discounts for a product.
     * Supports multiple discount tiers.
     */
    private void showDiscountDialog(Product p) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Manage Discount");
        dialog.setHeaderText("Manage Bulk Discounts for " + p.getName());

        // Set the button types
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        // Main container
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-pref-width: 500px;");

        // Initial content population
        updateDiscountDialogContent(p, mainBox);

        dialog.getDialogPane().setContent(mainBox);
        
        // Refresh table when dialog closes
        dialog.setOnHidden(ev -> loadProductsData());

        dialog.showAndWait();
    }

    /**
     * Helper to rebuild the discount dialog content.
     */
    private void updateDiscountDialogContent(Product p, VBox mainBox) {
        mainBox.getChildren().clear();

        // Title
        Label titleLabel = new Label("Current Discount Rules:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        mainBox.getChildren().add(titleLabel);

        // Container for existing rules
        VBox rulesBox = new VBox(10);
        rulesBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Populate existing rules
        if (p.getDiscountRules().isEmpty()) {
            Label noRules = new Label("No discount rules yet. Add one below!");
            noRules.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
            rulesBox.getChildren().add(noRules);
        } else {
            // Sort rules by quantity for better display
            p.getDiscountRules().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(rule -> {
                    HBox ruleRow = new HBox(10);
                    ruleRow.setStyle("-fx-alignment: center-left; -fx-padding: 5;");
                    
                    Label ruleLabel = new Label("Buy " + rule.getKey() + "+ @ $" + String.format("%.2f", rule.getValue()));
                    ruleLabel.setStyle("-fx-font-size: 12px; -fx-min-width: 200px;");
                    
                    Button deleteBtn = new Button("Remove");
                    deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 10px;");
                    deleteBtn.setOnAction(ev -> {
                        try {
                            p.removeDiscount(rule.getKey());
                            productService.saveAllProducts();
                            // Refresh content in-place
                            updateDiscountDialogContent(p, mainBox);
                        } catch (Exception ex) {
                            showError("Failed to remove: " + ex.getMessage());
                        }
                    });
                    
                    ruleRow.getChildren().addAll(ruleLabel, deleteBtn);
                    rulesBox.getChildren().add(ruleRow);
                });
        }
        
        mainBox.getChildren().add(rulesBox);

        // Add new rule section
        Label addNewLabel = new Label("Add New Rule:");
        addNewLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        mainBox.getChildren().add(addNewLabel);

        GridPane addGrid = new GridPane();
        addGrid.setHgap(10);
        addGrid.setVgap(10);
        addGrid.setStyle("-fx-padding: 10; -fx-background-color: #e8f5e9; -fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-radius: 5;");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Min Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Discounted Price");

        Button addBtn = new Button("Add Rule");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addBtn.setOnAction(ev -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());
                p.setDiscount(qty, price);
                productService.saveAllProducts();
                // Refresh content in-place
                updateDiscountDialogContent(p, mainBox);
            } catch (NumberFormatException ex) {
                showError("Invalid input format.");
            } catch (Exception ex) {
                showError("Failed to add rule: " + ex.getMessage());
            }
        });

        addGrid.add(new Label("Min Quantity:"), 0, 0);
        addGrid.add(qtyField, 1, 0);
        addGrid.add(new Label("Price per Unit:"), 0, 1);
        addGrid.add(priceField, 1, 1);
        addGrid.add(addBtn, 1, 2);

        mainBox.getChildren().add(addGrid);

        // Clear all button if rules exist
        if (!p.getDiscountRules().isEmpty()) {
            Button clearAllBtn = new Button("Clear All Discounts");
            clearAllBtn.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
            clearAllBtn.setOnAction(ev -> {
                try {
                    p.clearDiscounts();
                    productService.saveAllProducts();
                    showInfo("All discounts cleared!");
                    // Refresh content in-place
                    updateDiscountDialogContent(p, mainBox);
                } catch (Exception ex) {
                    showError("Failed to clear: " + ex.getMessage());
                }
            });
            mainBox.getChildren().add(clearAllBtn);
        }
        
        // Resize dialog to fit new content
        if (mainBox.getScene() != null && mainBox.getScene().getWindow() != null) {
            mainBox.getScene().getWindow().sizeToScene();
        }
    }



    /**
     * Loads customers data from CSV into the customers table.
     */
    private void loadCustomersData() {
        System.out.println("[AdminController] Loading customers data...");
        try {
            if (customerService == null) {
                customerService = new CustomerService();
            }
            List<Customer> list = customerService.getAllCustomers();
            ObservableList<Customer> obs = FXCollections.observableArrayList(list);
            customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            // Actions column (Edit/Delete)
            customerActionCol.setCellFactory(col -> new TableCell<Customer, String>() {
                private final Button edit = new Button("Edit");
                private final Button del = new Button("Delete");
                private final HBox box = new HBox(5, edit, del);
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        Customer c = getTableView().getItems().get(idx);
                        edit.setOnAction(ev -> {
                            // Populate form fields for editing
                            customerNameField.setText(c.getName());
                            customerTypeField.setValue(c.getType());
                            customerPhoneField.setText(c.getPhone() != null ? c.getPhone() : "");
                            addCustomerBtn.setText("Update Customer");
                            editingCustomer = true;
                            editingCustomerId = c.getCustomerId();
                        });
                        del.setOnAction(ev -> {
                            try {
                                customerService.deleteCustomer(c.getCustomerId());
                                loadCustomersData();
                            } catch (Exception ex) {
                                System.err.println("[AdminController] Failed to delete customer: " + ex.getMessage());
                            }
                        });
                        setGraphic(box);
                    }
                }
            });
            customersTable.setItems(obs);
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading customers: " + e.getMessage());
        }
    }

    /**
     * Handle adding/updating a customer via form inputs
     */
    @FXML
    private void handleAddCustomer(ActionEvent event) {
        String name = customerNameField.getText().trim();
        String type = customerTypeField.getValue();
        String phone = customerPhoneField.getText().trim();
        
        if (name.isEmpty() || type == null || type.isEmpty()) {
            showError("Customer Name and Type are required.");
            return;
        }
        
        try {
            if (customerService == null) customerService = new CustomerService();
            
            if (editingCustomer && editingCustomerId != null) {
                // Update existing customer
                Customer existing = customerService.findCustomerById(editingCustomerId);
                if (existing != null) {
                    // Create new customer with same ID but updated info
                    Customer updated;
                    if ("VIP".equalsIgnoreCase(type)) {
                        updated = new VIPCustomer(editingCustomerId, name);
                    } else {
                        updated = new RegularCustomer(editingCustomerId, name);
                    }
                    updated.setPhone(phone);
                    customerService.updateCustomer(updated);
                    showInfo("Customer updated successfully!");
                    editingCustomer = false;
                    editingCustomerId = null;
                    addCustomerBtn.setText("Add Customer");
                } else {
                    showError("Customer not found for update.");
                }
            } else {
                // Add new customer
                Customer c;
                if ("VIP".equalsIgnoreCase(type)) {
                    c = new VIPCustomer(null, name);
                } else {
                    c = new RegularCustomer(null, name);
                }
                c.setPhone(phone);
                customerService.addCustomer(c);
                showInfo("Customer added successfully! ID: " + c.getCustomerId());
            }
            
            // Clear form fields
            customerNameField.clear();
            customerTypeField.setValue(null);
            customerPhoneField.clear();
            editingCustomer = false;
            editingCustomerId = null;
            addCustomerBtn.setText("Add Customer");
            
            // Reload table
            loadCustomersData();
        } catch (Exception ex) {
            showError("Failed to add/update customer: " + ex.getMessage());
        }
    }
    

    /**
     * Loads users data from CSV into the users table.
     */
    private void loadUsersData() {
        System.out.println("[AdminController] Loading users data...");
        try {
            List<String> lines = FileHandler.readCsv("data/users.csv");
            List<UserRow> rows = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] cols = line.split(",");
                if (cols.length >= 4) {
                    rows.add(new UserRow(cols[0].trim(), cols[1].trim(), cols[3].trim()));
                }
            }
            ObservableList<UserRow> obs = FXCollections.observableArrayList(rows);
            // configure columns
            // Note: user table columns are raw typed in FXML; set cell factories via lambda
            ((TableColumn<UserRow, String>) userIdCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().id));
            ((TableColumn<UserRow, String>) userUsernameCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username));
            ((TableColumn<UserRow, String>) userRoleCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().role));
            TableView<UserRow> ut = (TableView<UserRow>) usersTable;
            ut.setItems(obs);
            
            // Add Edit/Delete buttons to users action column
            ((TableColumn<UserRow, String>) userActionCol).setCellFactory(col -> new TableCell<UserRow, String>() {
                private final Button editBtn = new Button("Edit");
                private final Button delBtn = new Button("Delete");
                private final HBox box = new HBox(5, editBtn, delBtn);
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        UserRow u = getTableView().getItems().get(idx);
                        
                        editBtn.setOnAction(ev -> {
                            // Populate form fields for editing
                            userUsernameField.setText(u.username);
                            userRoleField.setValue(u.role);
                            userPasswordField.clear(); // Don't show password
                            addUserBtn.setText("Update User");
                            editingUser = true;
                            editingUserId = u.id;
                        });
                        
                        delBtn.setOnAction(ev -> {
                            try {
                                List<String> lines = FileHandler.readCsv("data/users.csv");
                                List<String> out = new ArrayList<>();
                                for (String line : lines) {
                                    String t = line.trim();
                                    if (t.isEmpty() || t.startsWith("#")) { out.add(line); continue; }
                                    String[] cols = line.split(",");
                                    if (cols.length >= 1 && cols[0].trim().equals(u.id)) {
                                        continue; // skip to delete
                                    } else {
                                        out.add(line);
                                    }
                                }
                                FileHandler.writeCsv("data/users.csv", out);
                                loadUsersData();
                                showInfo("User deleted: " + u.id);
                            } catch (Exception ex) {
                                showError("Failed to delete user: " + ex.getMessage());
                            }
                        });
                        setGraphic(box);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading users: " + e.getMessage());
        }
    }

    // --- Service references ---
    private ProductService productService;
    private DiscountService discountService;
    private CustomerService customerService;
    // Edit state for products
    private boolean editingProduct = false;
    private String editingProductId = null;

    // Alert/Dialog helper methods
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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

    // Small helper classes for table rows
    private static class UserRow {
        final String id;
        final String username;
        final String role;

        UserRow(String id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
    }

    private static class DiscountRow {
        final String code;      // Product ID - Name
        final String type;      // Quantity threshold
        final String value;     // Price at this threshold
        final String min;       // Unused, kept for compatibility
        final String productId; // Product ID for deletion

        DiscountRow(String code, String type, String value, String productId) {
            this.code = code;
            this.type = type;
            this.value = value;
            this.min = "";
            this.productId = productId;
        }
        public String getCode() { return code; }
        public String getType() { return type; }
        public String getValue() { return value; }
        public String getMin() { return min; }
        public String getProductId() { return productId; }
    }
}
