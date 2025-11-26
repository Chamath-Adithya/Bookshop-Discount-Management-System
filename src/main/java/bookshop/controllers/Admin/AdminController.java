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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private Button discountsBtn;

    @FXML
    private Button customersBtn;

    @FXML
    private Button usersBtn;

    @FXML
    private Button reportsBtn;
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
    private TableColumn<Product, String> productActionCol;
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Discounts Tab">
    @FXML
    private TextField discountCodeField;

    @FXML
    private TextField discountTypeField;

    @FXML
    private TextField discountValueField;

    @FXML
    private TextField discountMinAmountField;

    @FXML
    private Button addDiscountBtn;

    @FXML
    private TableView<?> discountsTable;

    @FXML
    private TableColumn<?, ?> discountCodeCol;

    @FXML
    private TableColumn<?, ?> discountTypeCol;

    @FXML
    private TableColumn<?, ?> discountValueCol;

    @FXML
    private TableColumn<?, ?> discountMinCol;

    @FXML
    private TableColumn<?, ?> discountActionCol;
    //</editor-fold>

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
    //</editor-fold>

    //<editor-fold desc="FXML Annotations - Users Tab">
    @FXML
    private TextField userIdField;

    @FXML
    private TextField userUsernameField;

    @FXML
    private PasswordField userPasswordField;

    @FXML
    private TextField userRoleField;

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

    @FXML
    private Button inventoryReportBtn;

    @FXML
    private Button customersReportBtn;

    @FXML
    private TextArea reportTextArea;
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
        loadDiscountsData();
        loadCustomersData();
        loadUsersData();
    }

    // --- User edit support ---
    private boolean editingUser = false;
    private String editingUserId = null;

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
     * Handles clicking on the Discounts menu button.
     */
    @FXML
    private void handleDiscountsMenu(ActionEvent event) {
        System.out.println("[AdminController] Discounts menu clicked");
        contentTabPane.getSelectionModel().select(1); // Select Discounts tab
    }

    /**
     * Handles clicking on the Customers menu button.
     */
    @FXML
    private void handleCustomersMenu(ActionEvent event) {
        System.out.println("[AdminController] Customers menu clicked");
        contentTabPane.getSelectionModel().select(2); // Select Customers tab
    }

    /**
     * Handles clicking on the Users menu button.
     */
    @FXML
    private void handleUsersMenu(ActionEvent event) {
        System.out.println("[AdminController] Users menu clicked");
        contentTabPane.getSelectionModel().select(3); // Select Users tab
    }

    /**
     * Handles clicking on the Reports menu button.
     */
    @FXML
    private void handleReportsMenu(ActionEvent event) {
        System.out.println("[AdminController] Reports menu clicked");
        contentTabPane.getSelectionModel().select(4); // Select Reports tab
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

    // --- Discounts Tab Handlers ---

    /**
     * Handles adding a new discount.
     */
    @FXML
    private void handleAddDiscount(ActionEvent event) {
        if (productService == null) {
            try {
                productService = new ProductService();
            } catch (IOException e) {
                showError("Failed to load products: " + e.getMessage());
                return;
            }
        }
        
        // Get all product IDs
        List<String> productIds = new ArrayList<>();
        for (Product p : productService.getAllProducts()) {
            productIds.add(p.getProductId() + " - " + p.getName());
        }
        
        if (productIds.isEmpty()) {
            showError("No products available. Please add products first.");
            return;
        }
        
        // Step 1: Select product
        ChoiceDialog<String> productDlg = new ChoiceDialog<>(productIds.get(0), productIds);
        productDlg.setTitle("Add Bulk Discount");
        productDlg.setHeaderText("Select Product for Discount");
        productDlg.setContentText("Product:");
        
        productDlg.showAndWait().ifPresent(selectedProd -> {
            String productId = selectedProd.split(" - ")[0];
            
            // Step 2: Enter quantity threshold
            TextInputDialog qtyDlg = new TextInputDialog();
            qtyDlg.setTitle("Add Bulk Discount");
            qtyDlg.setHeaderText("Quantity Threshold");
            qtyDlg.setContentText("Min Quantity for Discount:");
            
            qtyDlg.showAndWait().ifPresent(qtyStr -> {
                try {
                    int quantity = Integer.parseInt(qtyStr);
                    
                    // Step 3: Enter discounted price
                    TextInputDialog priceDlg = new TextInputDialog();
                    priceDlg.setTitle("Add Bulk Discount");
                    priceDlg.setHeaderText("Discounted Price");
                    priceDlg.setContentText("Price per unit when buying " + quantity + "+ items:");
                    
                    priceDlg.showAndWait().ifPresent(priceStr -> {
                        try {
                            double price = Double.parseDouble(priceStr);
                            
                            Product prod = productService.findProductById(productId);
                            if (prod != null) {
                                prod.setDiscount(quantity, price);
                                productService.saveAllProducts();
                                showInfo("Discount added successfully!");
                                loadProductsData();
                                loadDiscountsData();
                            }
                        } catch (NumberFormatException ex) {
                            showError("Invalid price format.");
                        } catch (IOException ex) {
                            showError("Failed to save discount: " + ex.getMessage());
                        }
                    });
                } catch (NumberFormatException ex) {
                    showError("Invalid quantity format.");
                }
            });
        });
    }

    // --- Users Tab Handlers ---

    /**
     * Handles adding a new user.
     */
    @FXML
    private void handleAddUser(ActionEvent event) {
        String userId = userIdField.getText().trim();
        String username = userUsernameField.getText().trim();
        String password = userPasswordField.getText().trim();
        String role = userRoleField.getText().trim();

        System.out.println("[AdminController] Add/Update User: ID=" + userId + ", Username=" + username + ", Role=" + role);

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            System.out.println("[AdminController] User fields are empty!");
            return;
        }

        try {
            if (editingUser && editingUserId != null && editingUserId.equals(userId)) {
                // Update existing user in CSV
                List<String> lines = FileHandler.readCsv("data/users.csv");
                List<String> out = new ArrayList<>();
                for (String line : lines) {
                    String t = line.trim();
                    if (t.isEmpty() || t.startsWith("#")) { out.add(line); continue; }
                    String[] cols = line.split(",");
                    if (cols.length >= 1 && cols[0].trim().equals(userId)) {
                        out.add(String.format("%s,%s,%s,%s", userId, username, password, role));
                    } else {
                        out.add(line);
                    }
                }
                FileHandler.writeCsv("data/users.csv", out);
                System.out.println("[AdminController] User updated successfully!");
                editingUser = false;
                editingUserId = null;
                addUserBtn.setText("Add User");
            } else {
                // Append new user
                String line = String.format("%s,%s,%s,%s", userId, username, password, role);
                FileHandler.appendLine("data/users.csv", line);
                System.out.println("[AdminController] User added successfully!");
            }
        } catch (IOException ioe) {
            System.err.println("[AdminController] Failed to add/update user: " + ioe.getMessage());
        }

        // Clear fields
        userIdField.clear();
        userUsernameField.clear();
        userPasswordField.clear();
        userRoleField.clear();

        // Reload table
        loadUsersData();
    }

    // --- Reports Tab Handlers ---

    /**
     * Handles generating a sales report.
     */
    @FXML
    private void handleSalesReport(ActionEvent event) {
        System.out.println("[AdminController] Sales Report generated");
        String reportText = "=== SALES REPORT ===\n\n"
                          + "Total Sales: Rs. 50,000\n"
                          + "Total Transactions: 250\n"
                          + "Average Transaction: Rs. 200\n"
                          + "Top Product: Pen (500 units sold)\n"
                          + "Report Period: November 2025";
        reportTextArea.setText(reportText);
    }

    /**
     * Handles generating an inventory report.
     */
    @FXML
    private void handleInventoryReport(ActionEvent event) {
        System.out.println("[AdminController] Inventory Report generated");
        String reportText = "=== INVENTORY REPORT ===\n\n"
                          + "Total Products: 50\n"
                          + "Low Stock Items (< 10 units): 5\n"
                          + "Out of Stock Items: 2\n"
                          + "Total Inventory Value: Rs. 100,000\n"
                          + "Report Generated: " + java.time.LocalDateTime.now();
        reportTextArea.setText(reportText);
    }

    /**
     * Handles generating a customers report.
     */
    @FXML
    private void handleCustomersReport(ActionEvent event) {
        System.out.println("[AdminController] Customers Report generated");
        String reportText = "=== CUSTOMERS REPORT ===\n\n"
                          + "Total Customers: 500\n"
                          + "VIP Customers: 50\n"
                          + "Regular Customers: 450\n"
                          + "Active Customers (Last 30 days): 350\n"
                          + "Total Customer Spending: Rs. 500,000\n"
                          + "Report Generated: " + java.time.LocalDateTime.now();
        reportTextArea.setText(reportText);
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
    private void loadProductsData() {
        System.out.println("[AdminController] Loading products data...");
        try {
            if (productService == null) {
                productService = new ProductService();
            }
            List<Product> list = productService.getAllProducts();
            ObservableList<Product> obs = FXCollections.observableArrayList(list);
            // Setup columns if not already
            productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
            productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            productPriceCol.setCellValueFactory(new PropertyValueFactory<>("realPrice"));
            productQtyCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantity())));
            productsTable.setItems(obs);

            // Add action buttons (Delete) to action column
            productActionCol.setCellFactory(col -> new TableCell<Product, String>() {
                private final Button del = new Button("Delete");
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
                        del.setOnAction(ev -> {
                            try {
                                // Use ProductService to delete and persist
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
                        setGraphic(del);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading products: " + e.getMessage());
        }
    }

    /**
     * Loads discounts data from CSV into the discounts table.
     */
    private void loadDiscountsData() {
        System.out.println("[AdminController] Loading discounts data...");
        try {
            if (productService == null) {
                productService = new ProductService();
            }
            List<Product> products = productService.getAllProducts();
            // Flatten discount rules into simple rows
            List<DiscountRow> rows = new ArrayList<>();
            for (Product p : products) {
                p.getDiscountRules().forEach((qty, price) -> {
                    String productDisplay = p.getProductId() + " - " + p.getName();
                    rows.add(new DiscountRow(productDisplay, String.valueOf(qty), String.format("%.2f", price), p.getProductId()));
                });
            }
            ObservableList<DiscountRow> obs = FXCollections.observableArrayList(rows);
            // setup columns (map DiscountRow properties to columns)
            @SuppressWarnings("unchecked")
            TableView<DiscountRow> dt = (TableView<DiscountRow>) discountsTable;
            ((TableColumn<DiscountRow, String>) discountCodeCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCode()));
            ((TableColumn<DiscountRow, String>) discountTypeCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
            ((TableColumn<DiscountRow, String>) discountValueCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));
            ((TableColumn<DiscountRow, String>) discountMinCol).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMin()));
            
            // Add delete button
            @SuppressWarnings("unchecked")
            TableColumn<DiscountRow, String> actionCol = (TableColumn<DiscountRow, String>) discountActionCol;
            actionCol.setCellFactory(col -> new TableCell<DiscountRow, String>() {
                private final Button delBtn = new Button("Delete");
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    DiscountRow row = getTableView().getItems().get(getIndex());
                    delBtn.setStyle("-fx-padding: 5; -fx-font-size: 10; -fx-background-color: #cc0000; -fx-text-fill: white;");
                    delBtn.setOnAction(ev -> {
                        try {
                            ProductService ps = new ProductService();
                            Product p = ps.findProductById(row.getProductId());
                            if (p != null) {
                                int qty = Integer.parseInt(row.getType());
                                p.removeDiscount(qty);
                                ps.saveAllProducts();
                                loadDiscountsData();
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText(null);
                                alert.setContentText("Discount deleted successfully!");
                                alert.showAndWait();
                            }
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Failed to delete discount: " + ex.getMessage());
                            alert.showAndWait();
                        }
                    });
                    setGraphic(delBtn);
                }
            });
            
            dt.setItems(obs);
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading discounts: " + e.getMessage());
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
                            try {
                                // Show dialogs to edit name, type and phone
                                TextInputDialog nameDlg = new TextInputDialog(c.getName());
                                nameDlg.setTitle("Edit Customer");
                                nameDlg.setHeaderText("Edit Name for " + c.getCustomerId());
                                nameDlg.setContentText("Name:");
                                nameDlg.showAndWait().ifPresent(newName -> {
                                    c.setName(newName);
                                    // Type choice
                                    ChoiceDialog<String> typeDlg = new ChoiceDialog<>(c.getType(), "REGULAR", "VIP");
                                    typeDlg.setTitle("Edit Customer Type");
                                    typeDlg.setHeaderText("Select type for " + c.getCustomerId());
                                    typeDlg.setContentText("Type:");
                                    typeDlg.showAndWait().ifPresent(newType -> {
                                        // update via service (we will create a temporary customer object)
                                        Customer temp;
                                        if ("VIP".equalsIgnoreCase(newType)) temp = new VIPCustomer(c.getCustomerId(), c.getName());
                                        else temp = new RegularCustomer(c.getCustomerId(), c.getName());
                                        temp.setPhone(c.getPhone());
                                        try {
                                            // apply new name/phone/type
                                            temp.setName(newName);
                                            temp.setPhone(c.getPhone());
                                            customerService.updateCustomer(temp);
                                            loadCustomersData();
                                        } catch (Exception ex) {
                                            System.err.println("[AdminController] Failed to update customer: " + ex.getMessage());
                                        }
                                    });
                                });
                            } catch (Exception ex) {
                                System.err.println("[AdminController] Error editing customer: " + ex.getMessage());
                            }
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
     * Handle adding a new customer via dialog inputs
     */
    @FXML
    private void handleAddCustomer(ActionEvent event) {
        try {
            // No ID dialog needed, auto-generated
            TextInputDialog nameDlg = new TextInputDialog();
            nameDlg.setTitle("Add Customer");
            nameDlg.setHeaderText("New Customer Details");
            nameDlg.setContentText("Name:");
            
            nameDlg.showAndWait().ifPresent(name -> {
                ChoiceDialog<String> typeDlg = new ChoiceDialog<>("REGULAR", "REGULAR", "VIP");
                typeDlg.setTitle("Add Customer");
                typeDlg.setHeaderText("Customer Type");
                typeDlg.setContentText("Type:");
                
                typeDlg.showAndWait().ifPresent(type -> {
                    TextInputDialog phoneDlg = new TextInputDialog();
                    phoneDlg.setTitle("Add Customer");
                    phoneDlg.setHeaderText("Phone Number (Optional)");
                    phoneDlg.setContentText("Phone:");
                    
                    phoneDlg.showAndWait().ifPresent(phone -> {
                        try {
                            Customer c;
                            // Pass null for ID to let service generate it
                            if ("VIP".equalsIgnoreCase(type)) {
                                c = new VIPCustomer(null, name);
                            } else {
                                c = new RegularCustomer(null, name);
                            }
                            c.setPhone(phone);
                            
                            if (customerService == null) customerService = new CustomerService();
                            customerService.addCustomer(c);
                            loadCustomersData();
                            showInfo("Customer added successfully! ID: " + c.getCustomerId());
                        } catch (Exception ex) {
                            showError("Failed to add customer: " + ex.getMessage());
                        }
                    });
                });
            });
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
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
            // Selection listener to enable edit/update
            ut.setOnMouseClicked(ev -> {
                UserRow sel = ut.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    userIdField.setText(sel.id);
                    userUsernameField.setText(sel.username);
                    userRoleField.setText(sel.role);
                    addUserBtn.setText("Update User");
                    editingUser = true;
                    editingUserId = sel.id;
                }
            });
            // add delete button to users action column
            ((TableColumn<UserRow, String>) userActionCol).setCellFactory(col -> new TableCell<UserRow, String>() {
                private final Button del = new Button("Delete");
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        UserRow u = getTableView().getItems().get(idx);
                        del.setOnAction(ev -> {
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
                                System.out.println("[AdminController] User deleted: " + u.id);
                            } catch (Exception ex) {
                                System.err.println("[AdminController] Failed to delete user: " + ex.getMessage());
                            }
                        });
                        setGraphic(del);
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
