package live.craftmeet.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import live.craftmeet.authentication.dto.LoginResponse;
import live.craftmeet.authentication.jwt.JwtType;
import live.craftmeet.email.EmailService;
import live.craftmeet.user.dto.UserResponse;
import live.craftmeet.user.service.UserService;

/**
 * Unit tests for {@link AuthService}, using mocked collaborators.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private VerifyEmailTokenService verifyEmailTokenService;

    @Mock
    private ResetPasswordTokenService resetPasswordTokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void googleLogin_issuesAccessAndRefreshCookies() {
        UUID userId = UUID.randomUUID();
        UserResponse user = new UserResponse(userId, null, "a@b.com", true, Instant.now());
        UUID jti = UUID.randomUUID();
        when(userService.findOrCreateGoogleUser("a@b.com", "g-1")).thenReturn(user);
        when(refreshTokenService.generate(userId)).thenReturn(jti);
        when(jwtService.generate(userId, JwtType.ACCESS, null)).thenReturn("access-jwt");
        when(jwtService.generate(userId, JwtType.REFRESH, jti)).thenReturn("refresh-jwt");
        when(jwtService.buildToken("access-jwt", JwtType.ACCESS))
                .thenReturn(ResponseCookie.from("access_token", "access-jwt").build());
        when(jwtService.buildToken("refresh-jwt", JwtType.REFRESH))
                .thenReturn(ResponseCookie.from("refresh_token", "refresh-jwt").build());

        LoginResponse response = authService.googleLogin("a@b.com", "g-1");

        assertThat(response.user().id()).isEqualTo(userId);
        assertThat(response.accessToken().getValue()).isEqualTo("access-jwt");
        assertThat(response.refreshToken().getValue()).isEqualTo("refresh-jwt");
        verify(jwtService).generate(userId, JwtType.REFRESH, jti);
    }
}
