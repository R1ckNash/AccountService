package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaymentInvalidException extends ResponseStatusException {
    public PaymentInvalidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
