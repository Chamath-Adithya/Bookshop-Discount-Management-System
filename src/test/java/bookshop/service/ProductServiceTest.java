package bookshop.service;

import bookshop.model.Product;
import bookshop.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @TempDir
    Path tempDir;

    private Path productsFile;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        productsFile = tempDir.resolve("products.csv");
        // Create header
        FileHandler.writeCsv(productsFile.toString(), Collections.singletonList("product_id,product_name,real_price,discounts,quantity"));
        productService = new ProductService(productsFile.toString());
    }

    @Test
    void testAddProduct() throws IOException {
        Product p = new Product(null, "Test Book", 50.0);
        p.setQuantity(10);
        productService.addProduct(p);

        List<Product> products = productService.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("p01", products.get(0).getProductId());
        assertEquals("Test Book", products.get(0).getName());
        assertEquals(50.0, products.get(0).getRealPrice());
        assertEquals(10, products.get(0).getQuantity());

        // Verify file content
        List<String> lines = FileHandler.readCsv(productsFile.toString());
        assertEquals(2, lines.size()); // Header + 1 product
    }

    @Test
    void testLoadProducts() throws IOException {
        // Manually write to file
        String line = "p05,Existing Book,20.0,\"\",5";
        FileHandler.appendLine(productsFile.toString(), line);

        // Reload service
        productService = new ProductService(productsFile.toString());
        List<Product> products = productService.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("p05", products.get(0).getProductId());
        assertEquals("Existing Book", products.get(0).getName());
    }

    @Test
    void testUpdateProduct() throws IOException {
        Product p = new Product("p01", "Old Name", 10.0);
        productService.addProduct(p);

        p.setName("New Name");
        p.setRealPrice(15.0);
        productService.updateProduct(p);

        Product updated = productService.findProductById("p01");
        assertEquals("New Name", updated.getName());
        assertEquals(15.0, updated.getRealPrice());

        // Verify file content
        ProductService newService = new ProductService(productsFile.toString());
        Product loaded = newService.findProductById("p01");
        assertEquals("New Name", loaded.getName());
    }

    @Test
    void testDeleteProduct() throws IOException {
        Product p = new Product("p01", "To Delete", 10.0);
        productService.addProduct(p);

        productService.deleteProduct("p01");

        assertNull(productService.findProductById("p01"));
        assertEquals(0, productService.getAllProducts().size());

        // Verify file content
        ProductService newService = new ProductService(productsFile.toString());
        assertEquals(0, newService.getAllProducts().size());
    }

    @Test
    void testGenerateNextProductId() throws IOException {
        productService.addProduct(new Product("p01", "B1", 10));
        productService.addProduct(new Product("p03", "B2", 10));

        assertEquals("p04", productService.generateNextProductId());
    }
}
