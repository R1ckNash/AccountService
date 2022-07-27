package account.dto.responses;

import lombok.Getter;

@Getter
public class BaseSuccessfulResponse {
    private final String status;

    public BaseSuccessfulResponse(String status) {
        this.status = status;
    }
}
