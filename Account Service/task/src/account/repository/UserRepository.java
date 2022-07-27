package account.repository;

import account.dao.UserDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserDao, Long> {
    Optional<UserDao> findUserByEmail(String email);
    Optional<UserDao> findByEmailIgnoreCase(String email);
    List<UserDao> findByOrderByIdAsc();
}