package live.craftmeet.security;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import live.craftmeet.authentication.jwt.InvalidJwtException;
import live.craftmeet.authentication.jwt.JwtType;
import live.craftmeet.authentication.service.JwtService;
import live.craftmeet.exception.ErrorCode;
import live.craftmeet.exception.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private static final String ERROR_MESSAGE = "Invalid, missing or expired access token";
	private static final List<String> SHOULD_NOT_FILTER = List.of("/signup", "/verify", "/forgot-password",
			"/reset-password", "/login", "/refresh", "/logout", "/google");
	private final ObjectMapper objectMapper;

	public JwtFilter(JwtService jwtService, @Qualifier("jacksonJsonMapper") ObjectMapper objectMapper) {
		this.jwtService = jwtService;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {
		Optional<String> accessToken = getAccessToken(request);
		if (accessToken.isEmpty()) {
			writeUnauthorized(request, response);
			return;
		}
		String token = accessToken.get();
		try {
			Claims claims = jwtService.validate(token, JwtType.ACCESS);
			request.setAttribute("user_id", claims.getSubject());
			filterChain.doFilter(request, response);
		} catch (InvalidJwtException e) {
			writeUnauthorized(request, response);
		}
	}

	/**
	 * Writes a JSON error response indicating that authentication failed because
	 * the access token is missing, invalid, or expired.
	 *
	 * @param request
	 *                    the HTTP request whose path is reported in the error body
	 * @param response
	 *                    the HTTP response to which the error body is written
	 * @throws IOException
	 *                         if the response body cannot be written
	 */
	private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(),
				ErrorResponse.of(ErrorCode.UNAUTHORIZED, ERROR_MESSAGE, request.getRequestURI()));
	}

	/**
	 * Determines whether the JWT filter should be skipped for the supplied request.
	 * The request servlet path is checked against the configured path prefixes.
	 *
	 * @param request
	 *                    the HTTP request whose servlet path is checked
	 * @return true if the servlet path starts with any configured prefix, false
	 *         otherwise
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		for (String p : SHOULD_NOT_FILTER) {
			if (path.startsWith(p)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves the access token from the cookies of the supplied HTTP request.
	 * Searches the request cookies for the cookie whose name matches the access
	 * token type and returns its value when present.
	 *
	 * @param request
	 *                    the HTTP request whose cookies are searched for the access
	 *                    token
	 * @return an Optional containing the access token value if the matching cookie
	 *         is present, or an empty Optional if the request has no cookies or no
	 *         matching access token cookie
	 *         is found
	 */
	private Optional<String> getAccessToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}
		for (Cookie cookie : cookies) {
			if (Objects.equals(cookie.getName(), JwtType.ACCESS.getType())) {
				return Optional.ofNullable(cookie.getValue());
			}
		}
		return Optional.empty();
	}
}
