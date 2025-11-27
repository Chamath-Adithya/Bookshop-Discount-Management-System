package bookshop.strategy;

import bookshop.model.Customer;
import bookshop.model.Product;

public class VIPDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double currentTotal, Product product, int quantity, Customer customer) {
        // Apply VIP discount if applicable
        // This strategy applies on top of the current total
        
        if (customer != null && "VIP".equalsIgnoreCase(customer.getType())) {
            // VIPs get an additional discount. 
            // In the original code, VIPCustomer had a 5% discount (0.05).
            // We can hardcode it here or get it from the customer if we add a field.
            // For now, let's use the logic that was in VIPCustomer.
            
            double discountRate = 0.05; // 5%
            // Or use customer.getBaseDiscountRate() if that's what it's for?
            // Customer class has 'baseDiscountRate'. Let's check if we should use that.
            
            if (customer.getBaseDiscountRate() > 0) {
                discountRate = customer.getBaseDiscountRate();
            }
            
            return currentTotal * (1.0 - discountRate);
        }
        
        return currentTotal;
    }
}
