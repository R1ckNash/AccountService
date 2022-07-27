package account.services;

import account.dao.UserDao;
import account.dto.responses.ChangePasswordResponse;
import account.dto.UserResponseDto;
import account.exception.PasswordIvalidException;
import account.exception.UserExistException;
import account.model.SecurityAction;
import account.model.UserRole;
import account.repository.UserRepository;
import account.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthorizationService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final Set<String> breachedPasswords;
  private final SecurityService securityService;
  private final UserMapper mapper;

  {
    breachedPasswords =
        Set.of(
            "PasswordForJanuary",
            "PasswordForFebruary",
            "PasswordForMarch",
            "PasswordForApril",
            "PasswordForMay",
            "PasswordForJune",
            "PasswordForJuly",
            "PasswordForAugust",
            "PasswordForSeptember",
            "PasswordForOctober",
            "PasswordForNovember",
            "PasswordForDecember");
  }

  @Autowired
  public AuthorizationService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, SecurityService securityService, UserMapper mapper) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.securityService = securityService;
    this.mapper = mapper;
  }

  public ResponseEntity<UserResponseDto> findByEmail(String email) {
    return ResponseEntity.ok(userService.findByEmailDto(email));
  }

  public UserResponseDto addUser(UserDao user) {
    if (userService.isUserExists(user.getEmail())) {
      throw new UserExistException();
    }

    validatePassword(user.getPassword());

    UserRole userRole = userRepository.count() == 0 ? UserRole.ROLE_ADMINISTRATOR : UserRole.ROLE_USER;
    user.addRole(userRole);
    user.setEmail(user.getEmail().toLowerCase(Locale.ROOT));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    UserDao userDao = userRepository.save(user);

    securityService.createSecurityEvent(
            SecurityAction.CREATE_USER,
            "Anonymous",
            user.getEmail(),
            "/api/auth/signup");

    return mapper.mapDaoToDto(userDao);
  }

  public ResponseEntity<ChangePasswordResponse> changePass(UserDetails details, String newPassword) {
    UserDao user = userService.findByEmailDao(details.getUsername());
    validatePassword(newPassword);
    if (passwordEncoder.matches(newPassword, user.getPassword())) {
      throw new PasswordIvalidException("The passwords must be different!");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    securityService.createSecurityEvent(
            SecurityAction.CHANGE_PASSWORD,
            user.getEmail(),
            user.getEmail(),
            "/api/auth/changepass");

    return ResponseEntity.ok(new ChangePasswordResponse(user.getEmail(),
            "The password has been updated successfully"));
  }

  public void validatePassword(String password) {
    if (password.length() < 12) {
      throw new PasswordIvalidException("Password length must be 12 chars minimum!");
    }
    if (breachedPasswords.contains(password)) {
      throw new PasswordIvalidException("The password is in the hacker's database!");
    }
  }
}
