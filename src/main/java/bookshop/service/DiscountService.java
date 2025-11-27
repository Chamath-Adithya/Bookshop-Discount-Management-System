package bookshop.service;

import java.io.IOException;

import bookshop.model.Product;

// Team Member C: Implement the DiscountService for updating product discounts.
public class DiscountService {

    /**
     * Adds a new discount rule to a product and overwrites the CSV file.
     * This is a complex read-modify-overwrite operation.
     */
    public void addDiscount(Product product, int quantity, double price) throws IOException {
        // Load products, find the product and update its discount rules, then save all products
        ProductService ps = new ProductService();
        Product existing = ps.findProductById(product.getProductId());
        if (existing == null) {
            throw new IOException("Product not found: " + product.getProductId());
        }
        existing.setDiscount(quantity, price);
        ps.saveAllProducts();
    }
}
