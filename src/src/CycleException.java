// Custom exception to handle circular dependencies (cycle errors)
public class CycleException extends Exception {
    // Constructor that accepts a message
    public CycleException(String message) {
        super(message);  // Pass the message to the parent Exception class
    }
}
