package account.dto;

import account.model.ChangeUserRoleOperation;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRoleRequestDto {
    @NotNull
    private String user;

    @NotNull
    private String role;

    @NotNull
    private ChangeUserRoleOperation operation;
}
