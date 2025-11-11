package com.vedicpooja.auth;

import com.vedicpooja.auth.dto.AuthResponse;
import com.vedicpooja.auth.dto.LoginRequest;
import com.vedicpooja.auth.dto.RegisterRequest;
import com.vedicpooja.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity&lt;AuthResponse&gt; register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(auth.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity&lt;AuthResponse&gt; login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity&lt;AuthResponse.UserInfo&gt; me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User u)) {
            return ResponseEntity.status(401).build();
        }
        var info = new AuthResponse.UserInfo(
                u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole().name()
        );
        return ResponseEntity.ok(info);
    }
}