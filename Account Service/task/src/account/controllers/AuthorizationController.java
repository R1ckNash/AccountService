package account.controllers;

import account.dao.UserDao;
import account.dto.ChangeUserRoleRequestDto;
import account.dto.DeleteUserResponseDto;
import account.dto.requests.ChangePasswordRequest;
import account.dto.responses.ChangePasswordResponse;
import account.dto.UserResponseDto;
import account.services.AuthorizationService;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final AuthorizationService authService;

    @Autowired
    public AuthorizationController(AuthorizationService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public UserResponseDto signup(@Valid @RequestBody UserDao user) {
        return authService.addUser(user);
    }

    @PostMapping("/changepass")
    public ResponseEntity<ChangePasswordResponse> postChangePass(@RequestBody @Valid ChangePasswordRequest newPassword,
                                                                 @AuthenticationPrincipal UserDetails details) {
         return authService.changePass(details, newPassword.getNewPassword());
    }
}