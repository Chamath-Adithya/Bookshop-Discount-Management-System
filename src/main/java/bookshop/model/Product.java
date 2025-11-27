package bookshop.model;

import java.util.HashMap;
import java.util.Map;

// Team Member A: Define the Product class to hold product information.
public class Product {
    private String productId;
    private String name;
    private double realPrice;
    private Map<Integer, Double> discountRules;
    private int quantity; // stock/quantity available

    // Constructor
    public Product(String productId, String name, double realPrice) {
        this.productId = productId;
        this.name = name;
        this.realPrice = realPrice;
        this.discountRules = new HashMap<>();
        this.quantity = 0;
    }

    // Constructor with discount rules
    public Product(String productId, String name, double realPrice, Map<Integer, Double> discountRules) {
        this.productId = productId;
        this.name = name;
        this.realPrice = realPrice;
        this.discountRules = new HashMap<>(discountRules);
        this.quantity = 0;
    }

    // Constructor with quantity
    public Product(String productId, String name, double realPrice, Map<Integer, Double> discountRules, int quantity) {
        this.productId = productId;
        this.name = name;
        this.realPrice = realPrice;
        this.discountRules = new HashMap<>(discountRules);
        this.quantity = quantity;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getRealPrice() {
        return realPrice;
    }

    public Map<Integer, Double> getDiscountRules() {
        return new HashMap<>(discountRules);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealPrice(double realPrice) {
        this.realPrice = realPrice;
    }

    /**
     * Set a discount rule for a specific quantity
     * @param quantity The quantity threshold for the discount
     * @param price The price per unit when buying this quantity
     */
    public void setDiscount(int quantity, double price) {
        discountRules.put(quantity, price);
    }

    /**
     * Remove a discount rule for a specific quantity
     * @param quantity The quantity threshold to remove
     */
    public void removeDiscount(int quantity) {
        discountRules.remove(quantity);
    }

    /**
     * Clear all discount rules
     */
    public void clearDiscounts() {
        discountRules.clear();
    }
}