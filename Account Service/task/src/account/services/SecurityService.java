package account.services;

import account.dao.SecurityEvent;
import account.dao.UserDao;
import account.model.SecurityAction;
import account.model.UserRole;
import account.repository.SecurityEventRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityService {
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private SecurityEventRepository securityEventRepository;

    @Autowired
    private UserRepository userRepository;

    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String email) {
        loginAttempts.put(email, 0);
    }

    @Transactional
    public void loginFailed(String email, HttpServletRequest request) {
        createSecurityEvent(
                SecurityAction.LOGIN_FAILED,
                email,
                request.getRequestURI(),
                request.getRequestURI());

        Optional<UserDao> optionalUser = userRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        UserDao user = optionalUser.get();

        if (user.getLocked()) {
            return;
        }

        int attempts = loginAttempts.getOrDefault(user.getEmail(), 0) + 1;

        if (attempts == MAX_FAILED_ATTEMPTS) {
            loginAttempts.put(user.getEmail(), 0);
            createSecurityEvent(
                    SecurityAction.BRUTE_FORCE,
                    user.getEmail(),
                    request.getRequestURI(),
                    request.getRequestURI());

            if (user.getUserRoles().stream().noneMatch(UserRole.ROLE_ADMINISTRATOR::equals)) {
                user.setLocked(true);
                userRepository.save(user);
                createSecurityEvent(
                        SecurityAction.LOCK_USER,
                        user.getEmail(),
                        "Lock user " + user.getEmail(),
                        request.getRequestURI());
            }
        } else {
            loginAttempts.put(user.getEmail(), attempts);
        }
    }

    public void createSecurityEvent(SecurityAction action, String subject, String object, String path) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setDate(LocalDateTime.now());
        securityEvent.setAction(action);
        securityEvent.setSubject(subject);
        securityEvent.setObject(object);
        securityEvent.setPath(path);
        securityEventRepository.save(securityEvent);
    }

    public Iterable<SecurityEvent> getSecurityEvents() {
        return securityEventRepository.findByOrderByIdAsc();
    }
}
