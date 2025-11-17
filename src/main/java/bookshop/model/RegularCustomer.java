package bookshop.model;

// Team Member B: Implement the RegularCustomer class, inheriting from Customer.
public class RegularCustomer extends Customer {
    public static final String TYPE = "REGULAR";

    // Constructor
    public RegularCustomer(String customerId, String name) {
        super(customerId, name, TYPE, 0.0);
    }

    @Override
    public double calculateFinalPrice(double totalAmount) {
        // Regular customers don't get additional discounts
        return totalAmount;
    }
}
