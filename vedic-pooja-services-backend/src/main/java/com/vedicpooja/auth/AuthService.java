package com.vedicpooja.auth;

import com.vedicpooja.auth.dto.AuthResponse;
import com.vedicpooja.auth.dto.LoginRequest;
import com.vedicpooja.auth.dto.RegisterRequest;
import com.vedicpooja.security.JwtService;
import com.vedicpooja.user.User;
import com.vedicpooja.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if ((request.getEmail() == null || request.getEmail().isBlank()) &amp;&amp;
            (request.getPhone() == null || request.getPhone().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone required");
        }
        if (request.getEmail() != null &amp;&amp; userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        if (request.getPhone() != null &amp;&amp; userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String subject = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String token = jwtService.generateToken(user.getId(), subject, user.getRole());
        return new AuthResponse(token, new AuthResponse.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name()
        ));
    }

    public AuthResponse login(LoginRequest request) {
        Optional&lt;User&gt; userOpt = Optional.empty();
        if (request.getEmail() != null &amp;&amp; !request.getEmail().isBlank()) {
            userOpt = userRepository.findByEmail(request.getEmail());
        } else if (request.getPhone() != null &amp;&amp; !request.getPhone().isBlank()) {
            userOpt = userRepository.findByPhone(request.getPhone());
        }
        User user = userOpt.orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String subject = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String token = jwtService.generateToken(user.getId(), subject, user.getRole());
        return new AuthResponse(token, new AuthResponse.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name()
        ));
    }
}