package bookshop.strategy;

import bookshop.model.Customer;
import bookshop.model.Product;

public interface DiscountStrategy {
    double applyDiscount(double currentTotal, Product product, int quantity, Customer customer);
}
