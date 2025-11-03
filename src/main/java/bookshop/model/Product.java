package bookshop.model;

import java.util.Map;

// Team Member A: Define the Product class to hold product information.
public class Product {
    private String productId;
    private String name;
    private double realPrice;
    private Map<Integer, Double> discountRules;
}