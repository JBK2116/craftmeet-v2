package live.craftmeet.live.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import live.craftmeet.authentication.jwt.JwtConfig;
import live.craftmeet.live.exception.ParticipantAuthenticationError;
import live.craftmeet.live.repository.ParticipantRepository;

@Service
public class ParticipantJwtService {

	private final JwtConfig jwtConfig;
	private final ParticipantRepository participantRepository;

	public ParticipantJwtService(JwtConfig jwtConfig, ParticipantRepository participantRepository) {
		this.jwtConfig = jwtConfig;
		this.participantRepository = participantRepository;
	}

	/**
	 * Generates a signed JWT for the specified participant and meeting.
	 * The token includes the participant as the subject, the meeting identifier as
	 * a claim,
	 * the configured issuer, the current issue time, and an expiration time based
	 * on the given meeting duration.
	 *
	 * @param meetingId
	 *                                   the identifier of the meeting to associate
	 *                                   with the token
	 * @param participantId
	 *                                   the identifier of the participant for whom
	 *                                   the token is generated
	 * @param meetingDurationMinutes
	 *                                   the meeting duration in minutes used to
	 *                                   calculate the token expiration time
	 * @return the compact, signed JWT string
	 */
	public String generate(UUID meetingId, UUID participantId, int meetingDurationMinutes) {
		Instant now = Instant.now();
		Date expiration = Date.from(now.plusMillis(TimeUnit.MINUTES.toMillis(meetingDurationMinutes)));
		JwtBuilder builder = Jwts.builder().issuer(jwtConfig.issuer()).subject(participantId.toString())
				.issuedAt(Date.from(now))
				.claim("meetingId", meetingId.toString())
				.expiration(expiration);
		return builder.signWith(jwtConfig.secretKey()).compact();
	}

	/**
	 * Validates the supplied signed JWT for the given meeting and returns the
	 * authenticated participant identifier.
	 * Validation verifies the token signature and issuer, confirms that the
	 * participant exists, and checks that the
	 * token's meeting identifier claim matches the supplied meeting identifier. If
	 * validation fails,
	 * a ParticipantAuthenticationError is thrown.
	 *
	 * @param token
	 *                      the signed JWT to validate
	 * @param meetingId
	 *                      the meeting identifier that the token must match
	 * @return the identifier of the authenticated participant
	 */
	public UUID validate(String token, UUID meetingId) {
		try {
			Claims claims = Jwts.parser().verifyWith(jwtConfig.secretKey()).build().parseSignedClaims(token)
					.getPayload();
			String issuer = claims.getIssuer();
			if (issuer == null || !issuer.equals(jwtConfig.issuer())) {
				throw new IllegalArgumentException("Invalid JWT issuer");
			}
			String subject = claims.getSubject();
			if (subject == null) {
				throw new IllegalArgumentException("JWT subject is missing");
			}
			UUID participantId = UUID.fromString(subject);
			if (!participantRepository.existsById(participantId)) {
				throw new IllegalArgumentException("Participant does not exist");
			}
			UUID claimsMeetingId = claims.get("meetingId", UUID.class);
			if (!claimsMeetingId.equals(meetingId)) {
				throw new IllegalArgumentException("JWT meeting ID does not match");
			}
			return participantId;
		} catch (JwtException | IllegalArgumentException e) {
			throw new ParticipantAuthenticationError(meetingId);
		}
	}

}