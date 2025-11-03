package bookshop.exceptions;

// Team Member E: Define a custom exception for invalid product errors.
public class InvalidProductException extends Exception {
    public InvalidProductException(String message) {
        super(message);
    }
}