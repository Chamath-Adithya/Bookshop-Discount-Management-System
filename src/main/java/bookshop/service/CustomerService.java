package bookshop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bookshop.model.Customer;
import bookshop.model.RegularCustomer;
import bookshop.model.VIPCustomer;
import bookshop.util.FileHandler;

// Team Member B: Implement service to manage customer data (e.g., loading from CSV).
public class CustomerService {
    private static final String CUSTOMERS_FILE_PATH = "data/customers.csv";
    private List<Customer> customers;

    public CustomerService() throws IOException {
        this.customers = new ArrayList<>();
        loadCustomers();
    }

    private void loadCustomers() throws IOException {
        List<String> lines = FileHandler.readCsv(CUSTOMERS_FILE_PATH);
        // Skip the header row
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] columns = line.split(",");
            if (columns.length >= 3) {
                String customerId = columns[0].trim();
                String customerName = columns[1].trim();
                String customerType = columns[2].trim();
                Customer customer;
                if ("VIP".equalsIgnoreCase(customerType)) {
                    customer = new VIPCustomer(customerId, customerName);
                } else {
                    customer = new RegularCustomer(customerId, customerName);
                }
                customers.add(customer);
            }
        }
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(id)) {
                return customer;
            }
        }
        return null;
    }
}
