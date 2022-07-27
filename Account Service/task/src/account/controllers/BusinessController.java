package account.controllers;

import account.dto.requests.PaymentRequest;
import account.dto.responses.BaseSuccessfulResponse;
import account.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BusinessController {

    private final PaymentService paymentService;

    @Autowired
    public BusinessController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<BaseSuccessfulResponse> postPayment(@RequestBody List<PaymentRequest> payments) {
        return paymentService.savePayment(payments);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<BaseSuccessfulResponse> putPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.putPayment(paymentRequest);
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<String> getPayment(@RequestParam(required = false) String period,
                                                            @AuthenticationPrincipal UserDetails details)  {
        return paymentService.getPayment(details, period);
    }
}
