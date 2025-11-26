package bookshop.strategy;

import bookshop.model.Customer;
import bookshop.model.Product;
import java.util.Map;

public class BulkDiscountStrategy implements DiscountStrategy {

    @Override
    public double applyDiscount(double currentTotal, Product product, int quantity, Customer customer) {
        // Calculate price based on quantity rules (Bulk Discount)
        // This strategy determines the base cost, so it might override currentTotal 
        // or be the first one applied.
        
        // Logic from BillingService.getUnitPrice
        double unitPrice = product.getRealPrice();
        Map<Integer, Double> rules = product.getDiscountRules();
        
        if (rules != null) {
            for (Map.Entry<Integer, Double> entry : rules.entrySet()) {
                if (quantity >= entry.getKey()) {
                    // Assuming the map stores the *discounted unit price*
                    // We take the best price (lowest) if there are multiple matching rules?
                    // The original logic just iterated, so order mattered or it overwrote.
                    // Usually maps are unordered unless LinkedHashMap/TreeMap.
                    // But let's assume we want the lowest price for the quantity.
                    // Or just follow original logic: "if quantity >= key, unitPrice = value".
                    // If map is unordered, this is flaky. But let's stick to the logic for now
                    // or improve it to find the best match.
                    
                    // Improvement: Find the rule with the highest quantity threshold that is <= actual quantity
                    // But the original code was:
                    // for (var entry : product.getDiscountRules().entrySet()) {
                    //     if (quantity >= entry.getKey()) {
                    //         unitPrice = entry.getValue();
                    //     }
                    // }
                    // This implies if multiple match, the last one visited wins. 
                    // I will improve this to be deterministic: pick the lowest price.
                    
                    if (entry.getValue() < unitPrice) {
                        unitPrice = entry.getValue();
                    }
                }
            }
        }
        
        return unitPrice * quantity;
    }
}
