package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class InvalidDateException extends ResponseStatusException {
    public InvalidDateException() {
        super(HttpStatus.BAD_REQUEST, "Invalid date");
    }
}
