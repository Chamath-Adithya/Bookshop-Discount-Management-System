package bookshop.model;

// Team Member B: Implement the VIPCustomer class with overridden discount logic.
public class VIPCustomer extends Customer {
    public static final String TYPE = "VIP";
    private static final double VIP_DISCOUNT_RATE = 0.05; // 5% discount for VIP customers

    // Constructor
    public VIPCustomer(String customerId, String name) {
        super(customerId, name, TYPE, VIP_DISCOUNT_RATE);
    }

    /**
     * Calculate final price after applying VIP discount
     * @param totalAmount The total amount before VIP discount
     * @return The final price after applying 5% VIP discount
     */
    @Override
    public double calculateFinalPrice(double totalAmount) {
        return totalAmount * (1 - VIP_DISCOUNT_RATE);
    }

    /**
     * Get the VIP discount rate
     * @return The VIP discount rate as a decimal (0.05 = 5%)
     */
    public double getVipDiscountRate() {
        return VIP_DISCOUNT_RATE;
    }
}