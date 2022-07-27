package account.services;

import account.dao.UserDao;
import account.dto.ChangeUserAccessRequestDto;
import account.dto.ChangeUserRoleRequestDto;
import account.dto.DeleteUserResponseDto;
import account.dto.UserResponseDto;
import account.dto.responses.BaseSuccessfulResponse;
import account.exception.UserNotExistException;
import account.model.ChangeUserAccessOperation;
import account.model.ChangeUserRoleOperation;
import account.model.SecurityAction;
import account.model.UserRole;
import account.repository.UserRepository;
import account.utils.UserMapper;
import account.utils.UserMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper mapper;
  private final SecurityService securityService;

  @Autowired
  public UserService(UserRepository userRepository, UserMapperImpl mapper, SecurityService securityService) {
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.securityService = securityService;
  }

  public UserResponseDto findByEmailDto(String email) {
    UserDao userDao = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(UserNotExistException::new);
    return mapper.mapDaoToDto(userDao);
  }

  public UserDao findByEmailDao(String email) {
    return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(UserNotExistException::new);
  }

  public boolean isUserExists(String email) {
    Optional<UserDao> userDao = userRepository.findByEmailIgnoreCase(email);
    return userDao.isPresent();
  }

  public List<UserResponseDto> getUsers() {
    return userRepository.findByOrderByIdAsc().stream()
            .map(user -> new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getLastname(),
                    user.getEmail(),
                    getSortedUserRoles(user.getUserRoles())))
            .collect(Collectors.toList());
  }

  private Set<UserRole> getSortedUserRoles(Set<UserRole> roles) {
    return roles.stream()
            .sorted(Comparator.comparing(Enum::toString))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public DeleteUserResponseDto deleteUser(String email, String currentUserCred) {

    UserDao currentUser = userRepository.findByEmailIgnoreCase(currentUserCred).get();

    if (currentUser.getEmail().equalsIgnoreCase(email)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
    }

    Optional<UserDao> optionalUser = userRepository.findByEmailIgnoreCase(email);

    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }

    UserDao user = optionalUser.get();
    userRepository.delete(user);

    securityService.createSecurityEvent(
            SecurityAction.DELETE_USER,
            currentUser.getEmail(),
            user.getEmail(),
            "/api/admin/user");

    return new DeleteUserResponseDto(user.getEmail(), "Deleted successfully!");
  }

  public UserResponseDto changeRole(ChangeUserRoleRequestDto changeUserRoleRequestDto, String currentUserName) {

    UserDao currentUser = userRepository.findByEmailIgnoreCase(currentUserName).get();

    UserRole requestUserRole;

    try {
      requestUserRole = UserRole.valueOf("ROLE_" + changeUserRoleRequestDto.getRole());
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
    }

    Optional<UserDao> optionalUser = userRepository.findByEmailIgnoreCase(changeUserRoleRequestDto.getUser());

    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }

    UserDao user = optionalUser.get();
    SecurityAction securityAction = null;

    if (changeUserRoleRequestDto.getOperation() == ChangeUserRoleOperation.REMOVE) {

      if (user.getUserRoles().stream().noneMatch(role -> role.equals(requestUserRole))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
      }

      if (requestUserRole == UserRole.ROLE_ADMINISTRATOR) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
      }

      if (user.getUserRoles().size() == 1) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
      }

      user.getUserRoles().remove(requestUserRole);
      securityAction = SecurityAction.REMOVE_ROLE;

    } else {
      if (user.getUserRoles().stream().anyMatch(UserRole.ROLE_ADMINISTRATOR::equals)) {
        if (requestUserRole != UserRole.ROLE_ADMINISTRATOR) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  "The user cannot combine administrative and business roles!");
        }
      } else {
        if (requestUserRole == UserRole.ROLE_ADMINISTRATOR) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  "The user cannot combine administrative and business roles!");
        }

        user.addRole(requestUserRole);
        securityAction = SecurityAction.GRANT_ROLE;
      }
    }

    userRepository.save(user);
    securityService.createSecurityEvent(
            securityAction,
            currentUser.getEmail(),
            (securityAction == SecurityAction.GRANT_ROLE ? "Grant role " : "Remove role ") +
                    changeUserRoleRequestDto.getRole() +
                    (securityAction == SecurityAction.GRANT_ROLE ? " to " : " from ") + user.getEmail(),
            "/api/admin/user/role");

    return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            getSortedUserRoles(user.getUserRoles()));
  }

  @Transactional
  public BaseSuccessfulResponse changeAccess(ChangeUserAccessRequestDto changeUserAccessRequestDto, String currentUserEmail) {

    UserDao currentUser = userRepository.findByEmailIgnoreCase(currentUserEmail).get();

    Optional<UserDao> optionalUser = userRepository.findByEmailIgnoreCase(changeUserAccessRequestDto.getUser());

    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }

    UserDao user = optionalUser.get();
    SecurityAction securityAction = null;

    if (changeUserAccessRequestDto.getOperation() == ChangeUserAccessOperation.LOCK) {
      if (user.getUserRoles().stream().anyMatch(UserRole.ROLE_ADMINISTRATOR::equals)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
      }

      user.setLocked(true);
      securityAction = SecurityAction.LOCK_USER;
    } else {
      user.setLocked(false);
      securityAction = SecurityAction.UNLOCK_USER;
    }

    userRepository.save(user);
    securityService.createSecurityEvent(
            securityAction,
            currentUser.getEmail(),
            (securityAction == SecurityAction.LOCK_USER ? "Lock" : "Unlock") + " user " + user.getEmail(),
            "/api/admin/user/role");

    return new BaseSuccessfulResponse("User " + user.getEmail() + " " + (user.getLocked() ? "locked!" : "unlocked!"));
  }
}
