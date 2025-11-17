package bookshop;

import bookshop.exceptions.InvalidProductException;
import bookshop.model.*;
import bookshop.service.BillingService;
import bookshop.service.CustomerService;
import bookshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Team Member E: Write unit tests for the BillingService.
@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    ProductService productService;

    @Mock
    CustomerService customerService;

    @InjectMocks
    BillingService billingService;

    private Product testProduct;
    private Customer regularCustomer;
    private Customer vipCustomer;

    @BeforeEach
    void setUp() {
        // 1. Create a reusable test product with discount rules
        Map<Integer, Double> discountRules = new HashMap<>();
        discountRules.put(5, 95.0);  // If quantity >= 5, price is 95.0
        discountRules.put(10, 80.0); // If quantity >= 10, price is 80.0
        testProduct = new Product("p01", "Test Book", 100.0, discountRules);

        // 2. Create reusable customer objects
        regularCustomer = new RegularCustomer("c01", "Regular Joe");
        vipCustomer = new VIPCustomer("c02", "VIP Jane");

        // 3. Define mock behavior: what should the mocked services return when called?
        // When productService.findProductByName("Test Book") is called, return our testProduct
        when(productService.findProductByName("Test Book")).thenReturn(testProduct);
        // When a non-existent product is requested, throw an exception
        when(productService.findProductByName("Unknown Book")).thenThrow(new InvalidProductException("Product not found: Unknown Book"));

        // When customerService is asked for customer "c01", return the regular customer
        when(customerService.findCustomerById("c01")).thenReturn(regularCustomer);
        // When customerService is asked for customer "c02", return the VIP customer
        when(customerService.findCustomerById("c02")).thenReturn(vipCustomer);
    }

    @Test
    void testCalculateTotal_RegularCustomer_NoDiscount() throws InvalidProductException {
        double total = billingService.calculateTotal("Test Book", 4, "c01");
        assertEquals(400.0, total); // 4 * 100.0 (no discount for qty 4)
    }

    @Test
    void testCalculateTotal_RegularCustomer_WithDiscount() throws InvalidProductException {
        // TODO: Implement test logic
        // Example: double total = billingService.calculateTotal("Test Book", 5, "c01");
        // assertEquals(475.0, total); // 5 * 95.0
    }

    @Test
    void testCalculateTotal_VIPCustomer_NoDiscount() throws InvalidProductException {
        // TODO: Implement test logic
    }

    @Test
    void testCalculateTotal_VIPCustomer_WithDiscount() throws InvalidProductException {
        // TODO: Implement test logic
    }

    @Test
    void testCalculateTotal_ProductNotFound() {
        // TODO: Implement test logic
        // Example: assertThrows(InvalidProductException.class, () -> billingService.calculateTotal("Unknown Book", 1, "c01"));
    }
}
