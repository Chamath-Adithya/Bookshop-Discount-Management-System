package bookshop.model;

// Team Member B: Create the abstract Customer class.
public abstract class Customer {
    protected String customerId;
    protected String name;
    protected String type;

    public abstract double calculateFinalPrice(double totalAmount);
}