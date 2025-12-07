package bookshop.model;

// Team Member C: Implement the Worker class, inheriting from User.
public class Worker extends User {
    public static final String ROLE = "CASHIER";

    // Constructor
    public Worker(String username, String password) {
        super(username, password, ROLE);
    }

    @Override
    public void performAction() {
        // Workers can process customer purchases
        // Implementation will be done in the service layer
    }
}