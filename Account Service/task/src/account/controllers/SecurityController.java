package account.controllers;

import account.dao.SecurityEvent;
import account.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/")
public class SecurityController {
    @Autowired
    private SecurityService securityService;

    @GetMapping("events")
    public Iterable<SecurityEvent> getSecurityEvents() {
        return securityService.getSecurityEvents();
    }
}