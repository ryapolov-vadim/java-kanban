package manager.exception;

public class ErrorResponse {
    private String erroreMessage;
    private Integer erroreCode;

    public ErrorResponse(String erroreMessage, Integer erroreCode) {
        this.erroreMessage = erroreMessage;
        this.erroreCode = erroreCode;
    }

    public String getErroreMessage() {
        return erroreMessage;
    }

    public void setErroreMessage(String erroreMessage) {
        this.erroreMessage = erroreMessage;
    }

    public Integer getErroreCode() {
        return erroreCode;
    }

    public void setErroreCode(Integer erroreCode) {
        this.erroreCode = erroreCode;
    }
}
