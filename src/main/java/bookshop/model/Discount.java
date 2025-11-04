package bookshop.model;

// Team Member A: Define the Discount class. Note: This may be unused if discount logic is handled directly with maps.
public class Discount {
    private String productId;
    private int quantity;
    private double price;

    // Constructor
    public Discount(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}