package account.services;

import account.dao.UserDao;
import account.dto.requests.PaymentRequest;
import account.dto.responses.BaseSuccessfulResponse;
import account.dto.responses.PaymentResponse;
import account.exception.PaymentInvalidException;
import account.exception.UserNotExistException;
import account.model.PaymentDao;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import account.utils.PaymentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final ObjectMapper jsonMapper;

    @Autowired
    public PaymentService(UserService userService, UserRepository userRepository, PaymentRepository paymentRepository,
                          PaymentMapper mapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.jsonMapper = new ObjectMapper()
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }

    @Transactional
    public ResponseEntity<BaseSuccessfulResponse> savePayment(List<PaymentRequest> paymentRequest) {
        validatePayments(paymentRequest);
        List<PaymentDao> paymentDaos = new ArrayList<>();
        paymentRequest.forEach(p -> {
            try {
                paymentDaos.add(mapper.mapPaymentRequestToPayment(p));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        paymentRepository.saveAll(paymentDaos);
        return ResponseEntity.ok(new BaseSuccessfulResponse("Added successfully!"));
    }

    @SneakyThrows
    public ResponseEntity<String> getPayment(UserDetails details, String period) {
        UserDao userDao = userService.findByEmailDao(details.getUsername());
        if (period != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-yyyy");
            YearMonth ym = YearMonth.parse(period, fmt);
            LocalDate dt = ym.atDay(1);
            Optional<PaymentDao> payment = paymentRepository.findByEmployeeIgnoreCaseAndPeriod(userDao.getEmail(), dt);

            if (payment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            PaymentResponse response = mapper.mapPaymentToPaymentResponse(payment.get());
            String responsePayment = jsonMapper.writeValueAsString(response);
            return ResponseEntity.ok(responsePayment);
        }

        Optional<List<PaymentDao>> payments = paymentRepository.findAllByEmployeeIgnoreCaseOrderByPeriodDesc(details.getUsername());
        if (payments.isEmpty()) {
            return ResponseEntity.ok(jsonMapper.writeValueAsString(Collections.emptyList()));
        }

        List<PaymentResponse> paymentResponses = payments.get().stream()
                .map(mapper::mapPaymentToPaymentResponse)
                .collect(Collectors.toList());

        String response = jsonMapper.writeValueAsString(paymentResponses);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @SneakyThrows
    public ResponseEntity<BaseSuccessfulResponse> putPayment(PaymentRequest paymentRequest) {
        validatePayments(Collections.singletonList(paymentRequest));
        PaymentDao newPaymentDao = mapper.mapPaymentRequestToPayment(paymentRequest);
        Optional<PaymentDao> oldPayment = paymentRepository.findByEmployeeIgnoreCaseAndPeriod(newPaymentDao.getEmployee(),
                newPaymentDao.getPeriod());
        if (oldPayment.isEmpty()) {
            throw new PaymentInvalidException("Payment not found");
        }
        paymentRepository.delete(oldPayment.get());
        paymentRepository.save(newPaymentDao);
        return ResponseEntity.ok(new BaseSuccessfulResponse("Updated successfully!"));
    }

    public void validatePayments(List<PaymentRequest> payments) {
        payments.forEach(p ->
                        userRepository.findByEmailIgnoreCase(p.getEmployee())
                                .orElseThrow(UserNotExistException::new));

        payments.stream()
                .filter(p -> p.getSalary() < 0)
                .findAny()
                .ifPresent(e -> {
                    throw new PaymentInvalidException("Salary cannot be less than 0");});

        List<Map<String, String>> employeePeriodPair = new ArrayList<>();
        payments.forEach(payment -> employeePeriodPair.add(Collections.singletonMap(payment.getEmployee(), payment.getPeriod())));
        Set<List<Map<String, String>>> pairsWithoutDuplicates = new HashSet(employeePeriodPair);
        if (pairsWithoutDuplicates.size() != employeePeriodPair.size()) {
            throw new PaymentInvalidException("Remove duplicates");
        }
    }
}
