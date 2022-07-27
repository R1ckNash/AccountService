package account.controllers;

import account.dto.ChangeUserAccessRequestDto;
import account.dto.ChangeUserRoleRequestDto;
import account.dto.DeleteUserResponseDto;
import account.dto.UserResponseDto;
import account.dto.responses.BaseSuccessfulResponse;
import account.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ServiceController {

    private final UserService userService;

    public ServiceController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/user/{email}")
    public DeleteUserResponseDto deleteUser(@PathVariable String email,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.deleteUser(email, userDetails.getUsername());
    }

    @PutMapping("/user/role")
    public UserResponseDto changeRole(@RequestBody @Valid ChangeUserRoleRequestDto changeUserRoleRequestDto,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        return userService.changeRole(changeUserRoleRequestDto, userDetails.getUsername());
    }

    @PutMapping("/user/access")
    public BaseSuccessfulResponse changeAccess(@Valid @RequestBody ChangeUserAccessRequestDto changeUserAccessRequestDto,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return userService.changeAccess(changeUserAccessRequestDto, userDetails.getUsername());
    }
}
