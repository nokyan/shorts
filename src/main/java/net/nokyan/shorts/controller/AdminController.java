package net.nokyan.shorts.controller;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.nokyan.shorts.service.AdminService;

@Slf4j
@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    @Value("${admin-auth-token}")
    private String adminAuthToken;

    @Autowired
    private AdminService adminService;

    private boolean validateAdminToken(String token) {
        return StringUtils.hasText(token) && token.equals(adminAuthToken);
    }

    @GetMapping("/blocklist")
    public ResponseEntity<String> getBlocklist(@RequestHeader("ADMIN_AUTH") String adminAuth) {
        if (!validateAdminToken(adminAuth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok()
                .body(adminService.getBlocklist().stream().map(pattern -> pattern.toString())
                        .collect(Collectors.joining("\n")));
    }

    @PostMapping("/blocklist")
    public ResponseEntity<String> appendBlocklist(@RequestHeader("ADMIN_AUTH") String adminAuth,
            @RequestBody String regexes) {
        if (!validateAdminToken(adminAuth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Pattern> patterns = regexes.lines().map(regexString -> Pattern.compile(regexString)).toList();
            adminService.extendBlocklist(patterns);
            return ResponseEntity.ok().build();
        } catch (PatternSyntaxException e) {
            return ResponseEntity.badRequest().build();

        }
    }

    @DeleteMapping("/blocklist")
    public ResponseEntity<String> popBlocklist(@RequestHeader("ADMIN_AUTH") String adminAuth,
            @RequestBody String regexes) {
        if (!validateAdminToken(adminAuth)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Pattern> patterns = regexes.lines().map(regexString -> Pattern.compile(regexString)).toList();
            adminService.removeBlocklist(patterns);
            return ResponseEntity.ok().build();
        } catch (PatternSyntaxException e) {
            return ResponseEntity.badRequest().build();

        }
    }

}
