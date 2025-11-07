package bookshop.model;

// Team Member B: Create the abstract Customer class.
public abstract class Customer {
    protected String customerId;
    protected String name;
    protected String type;
    protected double baseDiscountRate;

    // Constructor
    public Customer(String customerId, String name, String type,double baseDiscountRate) {
        this.customerId = customerId;
        this.name = name;
        this.type = type;
        this.baseDiscountRate = baseDiscountRate;
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getBaseDiscountRate() {
        return baseDiscountRate;
    }

    // Setters
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void setType(String type) {
        this.type = type;
    }


    /**
     * Calculate the final price after applying any customer specific discounts
     * @param totalAmount The total amount before customer specific discounts
     * @return The final price after applying customer specific discounts
     */
    public abstract double calculateFinalPrice(double totalAmount);
}