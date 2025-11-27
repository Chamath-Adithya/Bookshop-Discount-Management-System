package bookshop.service;

import bookshop.model.Customer;
import bookshop.model.RegularCustomer;
import bookshop.model.VIPCustomer;
import bookshop.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @TempDir
    Path tempDir;

    private Path customersFile;
    private CustomerService customerService;

    @BeforeEach
    void setUp() throws IOException {
        customersFile = tempDir.resolve("customers.csv");
        FileHandler.writeCsv(customersFile.toString(), Collections.singletonList("customer_id,customer_name,customer_type,phone"));
        customerService = new CustomerService(customersFile.toString());
    }

    @Test
    void testAddRegularCustomer() throws IOException {
        Customer c = new RegularCustomer(null, "John Doe");
        c.setPhone("1234567890");
        customerService.addCustomer(c);

        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(1, customers.size());
        assertEquals("c01", customers.get(0).getCustomerId());
        assertEquals("John Doe", customers.get(0).getName());
        assertEquals("REGULAR", customers.get(0).getType());
    }

    @Test
    void testAddVIPCustomer() throws IOException {
        Customer c = new VIPCustomer(null, "Jane Doe");
        customerService.addCustomer(c);

        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(1, customers.size());
        assertTrue(customers.get(0) instanceof VIPCustomer);
        assertEquals("VIP", customers.get(0).getType());
    }

    @Test
    void testLoadCustomers() throws IOException {
        String line = "c05,Existing User,VIP,0987654321";
        FileHandler.appendLine(customersFile.toString(), line);

        customerService = new CustomerService(customersFile.toString());
        Customer c = customerService.findCustomerById("c05");
        assertNotNull(c);
        assertEquals("Existing User", c.getName());
        assertTrue(c instanceof VIPCustomer);
    }

    @Test
    void testUpdateCustomer() throws IOException {
        Customer c = new RegularCustomer("c01", "Old Name");
        customerService.addCustomer(c);

        c.setName("New Name");
        customerService.updateCustomer(c);

        Customer updated = customerService.findCustomerById("c01");
        assertEquals("New Name", updated.getName());
    }

    @Test
    void testDeleteCustomer() throws IOException {
        Customer c = new RegularCustomer("c01", "To Delete");
        customerService.addCustomer(c);

        customerService.deleteCustomer("c01");
        assertNull(customerService.findCustomerById("c01"));
    }

    @Test
    void testValidation() {
        Customer c = new RegularCustomer("c01", ""); // Empty name
        assertThrows(IllegalArgumentException.class, () -> customerService.addCustomer(c));

        Customer c2 = new RegularCustomer("c02", "Valid");
        c2.setPhone("123"); // Invalid phone
        assertThrows(IllegalArgumentException.class, () -> customerService.addCustomer(c2));
    }
}
