package bookshop.model;

// Team Member B: Implement the RegularCustomer class, inheriting from Customer.
public class RegularCustomer extends Customer {
    @Override
    public double calculateFinalPrice(double totalAmount) {
        return totalAmount;
    }
}