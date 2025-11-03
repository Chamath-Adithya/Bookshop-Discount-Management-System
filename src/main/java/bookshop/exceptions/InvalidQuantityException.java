package bookshop.exceptions;

// Team Member E: Define a custom exception for invalid quantity errors.
public class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}