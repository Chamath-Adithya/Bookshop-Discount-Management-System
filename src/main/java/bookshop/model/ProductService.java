package bookshop.service;

import bookshop.model.Product;
import bookshop.util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Team Member C: Implement the ProductService to load and manage product data.
public class ProductService {
    private static final String PRODUCTS_FILE_PATH = "data/products.csv";
    private List<Product> products;

    public ProductService() throws IOException {
        this.products = new ArrayList<>();
        loadProducts();
    }

    private void loadProducts() throws IOException {
        // TODO: Implement this method based on PLAN.md
        // 1. Read lines from PRODUCTS_FILE_PATH using FileHandler.readCsv()
        // 2. Skip the header row.
        // 3. For each data row:
        //    a. Parse the columns (product_id, product_name, real_price, discounts_str).
        //    b. Use FileHandler.parseDiscountString(discounts_str) to get the discount map.
        //    c. Create a new Product object.
        //    d. Add the product to the 'products' list.
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findProductByName(String name) {
        // TODO: Implement this method
        return null;
    }

    public Product findProductById(String id) {
        // TODO: Implement this method
        return null;
    }
}