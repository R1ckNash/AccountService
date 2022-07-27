package account.model;

import account.dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PaymentDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employee;
    private LocalDate period;
    private Long salary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDao userDao;
}
