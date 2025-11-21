package bookshop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bookshop.exceptions.InvalidProductException;
import bookshop.model.Product;
import bookshop.util.FileHandler;

// Team Member C: Implement the ProductService to load and manage product data.
public class ProductService {
    private static final String PRODUCTS_FILE_PATH = "data/products.csv";
    private List<Product> products;

    public ProductService() throws IOException {
        this.products = new ArrayList<>();
        loadProducts();
    }

    private void loadProducts() throws IOException {
        List<String> lines = FileHandler.readCsv(PRODUCTS_FILE_PATH);
        // Skip the header row
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] columns = line.split(",");
            if (columns.length >= 4) {
                String productId = columns[0].trim();
                String productName = columns[1].trim();
                double realPrice = Double.parseDouble(columns[2].trim());
                String discountsStr = columns[3].trim();
                // Remove surrounding quotes if present
                if (discountsStr.startsWith("\"") && discountsStr.endsWith("\"")) {
                    discountsStr = discountsStr.substring(1, discountsStr.length() - 1);
                }
                Map<Integer, Double> discountRules = FileHandler.parseDiscountString(discountsStr);
                int qty = 0;
                if (columns.length >= 5) {
                    try {
                        qty = Integer.parseInt(columns[4].trim());
                    } catch (NumberFormatException nfe) {
                        qty = 0;
                    }
                }
                Product product = new Product(productId, productName, realPrice, discountRules, qty);
                products.add(product);
            }
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findProductByName(String name) throws InvalidProductException {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        throw new InvalidProductException("Product not found: " + name);
    }

    public Product findProductById(String id) {
        for (Product product : products) {
            if (product.getProductId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Add a product to the CSV file and in-memory list.
     */
    public void addProduct(Product product) throws IOException {
        // Serialize product line: id,name,realPrice,"discounts"
        String discounts = FileHandler.serializeDiscountMap(product.getDiscountRules());
        String line = String.format("%s,%s,%.2f,\"%s\",%d", product.getProductId(), product.getName(), product.getRealPrice(), discounts == null ? "" : discounts, product.getQuantity());
        FileHandler.appendLine(PRODUCTS_FILE_PATH, line);
        this.products.add(product);
    }

    /**
     * Delete a product by id and persist changes.
     */
    public void deleteProduct(String productId) throws IOException {
        products.removeIf(p -> p.getProductId().equals(productId));
        saveAllProducts();
    }

    /**
     * Update an existing product (matching by id) and persist.
     */
    public void updateProduct(Product updated) throws IOException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(updated.getProductId())) {
                products.set(i, updated);
                saveAllProducts();
                return;
            }
        }
        // if not found, add
        addProduct(updated);
    }

    /**
     * Save all products to the CSV file (overwrite).
     */
    public void saveAllProducts() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("product_id,product_name,real_price,discounts,quantity");
        for (Product p : products) {
            String discounts = FileHandler.serializeDiscountMap(p.getDiscountRules());
            String line = String.format("%s,%s,%.2f,\"%s\",%d", p.getProductId(), p.getName(), p.getRealPrice(), discounts == null ? "" : discounts, p.getQuantity());
            lines.add(line);
        }
        FileHandler.writeCsv(PRODUCTS_FILE_PATH, lines);
    }
}
