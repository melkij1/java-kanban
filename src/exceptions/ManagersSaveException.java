package exceptions;

public class ManagersSaveException extends RuntimeException {
    
    public ManagersSaveException() {
        super();
    }

    public ManagersSaveException(String message) {
        super(message);
    }
}
