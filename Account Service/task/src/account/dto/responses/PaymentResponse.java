package account.dto.responses;

import account.utils.SingleElementCollectionsUnwrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentResponse {
    private String name;
    private String lastname;
    private String period;
    private String salary;
}
