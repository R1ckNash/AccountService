package account.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ChangePasswordRequest {
    @JsonProperty("new_password")
    @NotEmpty
    private String newPassword;
}
