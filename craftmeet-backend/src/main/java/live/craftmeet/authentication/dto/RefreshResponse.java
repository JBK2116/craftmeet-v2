package live.craftmeet.authentication.dto;

import org.springframework.http.ResponseCookie;

public record RefreshResponse(ResponseCookie accessToken, ResponseCookie refreshToken) {
}
