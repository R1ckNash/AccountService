package account.repository;

import account.model.PaymentDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<PaymentDao, Long> {

    Optional<List<PaymentDao>> findAllByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);

    Optional<PaymentDao> findByEmployeeIgnoreCaseAndPeriod(String employee, LocalDate period);
}
