package live.craftmeet.authentication.dto;

import org.springframework.http.ResponseCookie;

public record LogoutResponse(ResponseCookie accessTokenCookie, ResponseCookie refreshTokenCookie) {
}
