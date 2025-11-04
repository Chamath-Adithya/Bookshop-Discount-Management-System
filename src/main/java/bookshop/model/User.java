package bookshop.model;

// Team Member A: Create the abstract User class.
public abstract class User {
    protected String username;
    protected String password;
    protected String role;

    // Constructor
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected void setRole(String role) {
        this.role = role;
    }

    /**
     * Abstract method to define user-specific actions
     */
    public abstract void performAction();
}