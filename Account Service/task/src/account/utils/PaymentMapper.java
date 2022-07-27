package account.utils;

import account.dto.requests.PaymentRequest;
import account.dto.responses.PaymentResponse;
import account.model.PaymentDao;

import java.text.ParseException;


public interface PaymentMapper {

    PaymentDao mapPaymentRequestToPayment(PaymentRequest paymentRequest) throws ParseException;

    PaymentResponse mapPaymentToPaymentResponse(PaymentDao paymentDao);
}
