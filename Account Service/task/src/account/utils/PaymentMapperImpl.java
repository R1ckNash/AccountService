package account.utils;

import account.dto.requests.PaymentRequest;
import account.dto.responses.PaymentResponse;
import account.model.PaymentDao;
import account.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentMapperImpl implements PaymentMapper {

  private final UserRepository userRepository;

  @Autowired
  public PaymentMapperImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @SneakyThrows
  public PaymentDao mapPaymentRequestToPayment(PaymentRequest paymentRequest){
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-yyyy");

    PaymentDao paymentDao = new PaymentDao();
    paymentDao.setUserDao(userRepository.findByEmailIgnoreCase(paymentRequest.getEmployee()).get());
    paymentDao.setEmployee(paymentRequest.getEmployee());

    YearMonth ym = YearMonth.parse(paymentRequest.getPeriod(), fmt);
    LocalDate dt = ym.atDay(1);

    paymentDao.setPeriod(dt);
    paymentDao.setSalary(paymentRequest.getSalary());

    return paymentDao;
  }

  @Override
  @SneakyThrows
  public PaymentResponse mapPaymentToPaymentResponse(PaymentDao paymentDao) {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("LLLL-yyyy");

    Long dollars = paymentDao.getSalary();

    PaymentResponse response = new PaymentResponse();
    response.setName(paymentDao.getUserDao().getName());
    response.setLastname(paymentDao.getUserDao().getLastname());
    response.setPeriod(paymentDao.getPeriod().format(fmt));
    response.setSalary(String.format("%s dollar(s) %s cent(s)", dollars / 100, dollars % 100));

    return response;
  }
}
