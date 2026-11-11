package live.craftmeet.authentication.service;

import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import live.craftmeet.authentication.dto.LoginRequest;
import live.craftmeet.authentication.dto.LoginResponse;
import live.craftmeet.authentication.dto.SignupRequest;
import live.craftmeet.authentication.jwt.JwtType;
import live.craftmeet.email.EmailService;
import live.craftmeet.user.dto.UserResponse;
import live.craftmeet.user.service.UserService;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final VerifyEmailTokenService verifyEmailTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    public AuthService(UserService userService, JwtService jwtService, VerifyEmailTokenService verifyEmailTokenService,
            RefreshTokenService refreshTokenService,
            EmailService emailService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.verifyEmailTokenService = verifyEmailTokenService;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        UserResponse userResponse = userService.createUser(signupRequest.email(), signupRequest.password(),
                signupRequest.username());
        String rawToken = verifyEmailTokenService.generate(userResponse.email());
        emailService.sendVerificationEmail(userResponse.email(), rawToken);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        verifyEmailTokenService.validate(rawToken);
    }

    @Transactional
    public void sendVerificationEmail(String email) {
        String rawToken = verifyEmailTokenService.generate(email);
        emailService.sendVerificationEmail(email, rawToken);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        UserResponse userResponse = userService.authenticate(loginRequest.email(), loginRequest.password());
        UUID jti = refreshTokenService.generate(userResponse.id());
        String accessJwt = jwtService.generate(userResponse.id(), JwtType.ACCESS, null);
        String refreshJwt = jwtService.generate(userResponse.id(), JwtType.REFRESH, jti);
        return new LoginResponse(userResponse, jwtService.buildToken(accessJwt, JwtType.ACCESS),
                jwtService.buildToken(refreshJwt, JwtType.REFRESH));
    }

}
