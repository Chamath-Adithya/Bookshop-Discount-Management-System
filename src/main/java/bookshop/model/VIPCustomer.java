package bookshop.model;

// Team Member B: Implement the VIPCustomer class with overridden discount logic.
public class VIPCustomer extends Customer {
    private static final double VIP_DISCOUNT_RATE = 0.05;

    @Override
    public double calculateFinalPrice(double totalAmount) {
        return totalAmount * (1 - VIP_DISCOUNT_RATE);
    }
}