package account.dto.requests;

import lombok.Data;

@Data
public class PaymentRequest {
    private String employee;
    private String period;
    private Long salary;
}
