package live.craftmeet.authentication;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import live.craftmeet.authentication.dto.LoginRequest;
import live.craftmeet.authentication.dto.LoginResponse;
import live.craftmeet.authentication.dto.SignupRequest;
import live.craftmeet.authentication.service.AuthService;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam(name = "token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, loginResponse.accessToken().toString())
                .header(HttpHeaders.SET_COOKIE, loginResponse.refreshToken().toString())
                .build();
    }

}
