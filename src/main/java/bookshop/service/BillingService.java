package bookshop.service;

import bookshop.exceptions.InvalidProductException;
import bookshop.model.Customer;
import bookshop.model.Product;

// Team Member D: Implement the core billing calculation logic.
public class BillingService {
    private final ProductService productService;
    private final CustomerService customerService;

    public BillingService(ProductService productService, CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;
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

        // Determine the unit price based on quantity and discount rules
        double unitPrice = getUnitPrice(product, quantity);

        // Calculate subtotal
        double subtotal = quantity * unitPrice;

        // Apply customer-specific discount
        double finalTotal = customer.calculateFinalPrice(subtotal);

        return finalTotal;
    }

    /**
     * Get the unit price for a product based on quantity and discount rules.
     * @param product The product.
     * @param quantity The quantity.
     * @return The unit price.
     */
    private double getUnitPrice(Product product, int quantity) {
        double unitPrice = product.getRealPrice(); // Default to real price
        for (var entry : product.getDiscountRules().entrySet()) {
            if (quantity >= entry.getKey()) {
                unitPrice = entry.getValue();
            }
        }
        return unitPrice;
    }
}
