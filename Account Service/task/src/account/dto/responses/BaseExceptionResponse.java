package account.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseExceptionResponse {
    @JsonProperty
    String timestamp;
    @JsonProperty
    int status;
    @JsonProperty
    String error;
    @JsonProperty
    String message;
    @JsonProperty
    String path;
}
