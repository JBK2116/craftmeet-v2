package live.craftmeet.authentication.dto;

import org.springframework.http.ResponseCookie;

import live.craftmeet.user.dto.UserResponse;

public record LoginResponse(UserResponse user, ResponseCookie accessToken, ResponseCookie refreshToken) {
}
