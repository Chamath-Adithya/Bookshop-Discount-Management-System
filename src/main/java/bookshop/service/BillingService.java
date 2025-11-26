package bookshop.service;

import bookshop.exceptions.InvalidProductException;
import bookshop.model.Customer;
import bookshop.model.Product;
import bookshop.strategy.BulkDiscountStrategy;
import bookshop.strategy.DiscountStrategy;
import bookshop.strategy.VIPDiscountStrategy;

import java.util.ArrayList;
import java.util.List;

// Team Member D: Implement the core billing calculation logic.
public class BillingService {
    private final ProductService productService;
    private final CustomerService customerService;
    private final List<DiscountStrategy> strategies;

    public BillingService(ProductService productService, CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;
        this.strategies = new ArrayList<>();
        // Add strategies in order of application
        this.strategies.add(new BulkDiscountStrategy());
        this.strategies.add(new VIPDiscountStrategy());
    }

    /**
     * Calculate the total price for a given product, quantity, and customer.
     * @param productName The name of the product.
     * @param quantity The quantity purchased.
     * @param customerId The ID of the customer.
     * @return The final total price after applying discounts.
     * @throws InvalidProductException If the product is not found.
     */
    public double calculateTotal(String productName, int quantity, String customerId) throws InvalidProductException {
        // Find the product
        Product product = productService.findProductByName(productName);
        if (product == null) {
            throw new InvalidProductException("Product not found: " + productName);
        }

        // Find the customer
        Customer customer = customerService.findCustomerById(customerId);
        if (customer == null) {
            throw new InvalidProductException("Customer not found: " + customerId);
        }

        double currentTotal = 0.0;

        // Apply strategies
        for (DiscountStrategy strategy : strategies) {
            currentTotal = strategy.applyDiscount(currentTotal, product, quantity, customer);
        }

        return currentTotal;
    }
}
