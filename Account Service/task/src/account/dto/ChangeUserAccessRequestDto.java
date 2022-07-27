package account.dto;

import account.model.ChangeUserAccessOperation;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserAccessRequestDto {
    @NotBlank
    private String user;

    @NotNull
    private ChangeUserAccessOperation operation;
}
