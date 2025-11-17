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
                Product product = new Product(productId, productName, realPrice, discountRules);
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
}
