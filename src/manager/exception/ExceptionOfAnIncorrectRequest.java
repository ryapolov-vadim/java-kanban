package manager.exception;

public class ExceptionOfAnIncorrectRequest extends RuntimeException {
    public ExceptionOfAnIncorrectRequest(String message) {
        super(message);
    }
}
