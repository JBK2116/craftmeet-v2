package live.craftmeet.authentication.oauth;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import live.craftmeet.authentication.dto.LoginResponse;
import live.craftmeet.authentication.service.AuthService;

/**
 * HTTP controller for the Google OAuth authorization-code flow.
 * Starts the flow by redirecting to Google and completes it by exchanging the
 * returned code, provisioning or linking the local account, and setting the
 * same authentication cookies used by password login.
 */
@RestController
public class GoogleAuthController {

    private static final String STATE_COOKIE = "oauth_state";

    private final AuthService authService;
    private final GoogleOAuthClient googleOAuthClient;
    private final String frontendUrl;

    public GoogleAuthController(AuthService authService, GoogleOAuthClient googleOAuthClient,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.authService = authService;
        this.googleOAuthClient = googleOAuthClient;
        this.frontendUrl = frontendUrl;
    }

    @GetMapping("/google")
    public ResponseEntity<Void> google() {
        String state = UUID.randomUUID().toString();
        ResponseCookie stateCookie = ResponseCookie.from(STATE_COOKIE, state)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .build();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, stateCookie.toString())
                .header(HttpHeaders.LOCATION, googleOAuthClient.buildAuthorizationUrl(state))
                .build();
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Void> googleCallback(@RequestParam("code") String code,
            @RequestParam("state") String state,
            @CookieValue(name = STATE_COOKIE, required = false) String stateCookie) {
        if (stateCookie == null || !stateCookie.equals(state)) {
            return errorRedirect();
        }
        try {
            GoogleUserInfo info = googleOAuthClient.exchangeCodeForUserInfo(code);
            LoginResponse login = authService.googleLogin(info.email(), info.sub());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.SET_COOKIE, clearStateCookie().toString())
                    .header(HttpHeaders.SET_COOKIE, login.accessToken().toString())
                    .header(HttpHeaders.SET_COOKIE, login.refreshToken().toString())
                    .header(HttpHeaders.LOCATION, frontendUrl)
                    .build();
        } catch (Exception e) {
            return errorRedirect();
        }
    }

    private ResponseEntity<Void> errorRedirect() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, clearStateCookie().toString())
                .header(HttpHeaders.LOCATION, frontendUrl + "/login?error=oauth_failed")
                .build();
    }

    private ResponseCookie clearStateCookie() {
        return ResponseCookie.from(STATE_COOKIE, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }
}
