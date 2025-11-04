package bookshop.model;

// Team Member C: Implement the Manager class, inheriting from User.
public class Manager extends User {
    public static final String ROLE = "MANAGER";

    // Constructor
    public Manager(String username, String password) {
        super(username, password, ROLE);
    }

    @Override
    public void performAction() {
        // Managers can manage products and discounts
        // Implementation will be done in the service layer
    }
}