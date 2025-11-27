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
                // optional phone column
                if (columns.length >= 4) {
                    String phone = columns[3].trim();
                    customer.setPhone(phone);
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

    /**
     * Persist the current customers list to CSV (overwrite)
     */
    public void saveAllCustomers() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("customer_id,customer_name,customer_type,phone");
        for (Customer c : customers) {
            String phone = c.getPhone() == null ? "" : c.getPhone();
            String line = String.format("%s,%s,%s,%s", c.getCustomerId(), c.getName(), c.getType(), phone);
            lines.add(line);
        }
        FileHandler.writeCsv(CUSTOMERS_FILE_PATH, lines);
    }

    public void addCustomer(Customer customer) throws IOException {
        if (customer.getCustomerId() == null || customer.getCustomerId().trim().isEmpty()) {
            customer.setCustomerId(generateNextCustomerId());
        }
        // append to file and add to memory
        String phone = customer.getPhone() == null ? "" : customer.getPhone();
        String line = String.format("%s,%s,%s,%s", customer.getCustomerId(), customer.getName(), customer.getType(), phone);
        FileHandler.appendLine(CUSTOMERS_FILE_PATH, line);
        this.customers.add(customer);
    }

    /**
     * Generates the next available customer ID based on the current list.
     * Assumes IDs are in the format "cXX" or "CXX" where XX is a number.
     * @return The next customer ID (e.g., "c06").
     */
    public String generateNextCustomerId() {
        int maxId = 0;
        for (Customer c : customers) {
            String id = c.getCustomerId();
            // Extract number from ID (e.g., "c01" -> 1)
            try {
                String numPart = id.replaceAll("[^0-9]", "");
                if (!numPart.isEmpty()) {
                    int num = Integer.parseInt(numPart);
                    if (num > maxId) {
                        maxId = num;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore malformed IDs
            }
        }
        // Generate next ID with padding (e.g., c06)
        return String.format("c%02d", maxId + 1);
    }

    public void updateCustomer(Customer customer) throws IOException {
        Customer existing = findCustomerById(customer.getCustomerId());
        if (existing != null) {
            // Name and phone can be updated directly
            existing.setName(customer.getName());
            existing.setPhone(customer.getPhone());
            // If type changed, replace the object with a new instance of the proper subclass
            if (!existing.getType().equalsIgnoreCase(customer.getType())) {
                Customer replacement;
                if ("VIP".equalsIgnoreCase(customer.getType())) {
                    replacement = new VIPCustomer(existing.getCustomerId(), customer.getName());
                } else {
                    replacement = new RegularCustomer(existing.getCustomerId(), customer.getName());
                }
                replacement.setPhone(customer.getPhone());
                // replace in list
                for (int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getCustomerId().equals(existing.getCustomerId())) {
                        customers.set(i, replacement);
                        break;
                    }
                }
            }
            saveAllCustomers();
        }
    }

    public void deleteCustomer(String customerId) throws IOException {
        customers.removeIf(c -> c.getCustomerId().equals(customerId));
        saveAllCustomers();
    }
}
