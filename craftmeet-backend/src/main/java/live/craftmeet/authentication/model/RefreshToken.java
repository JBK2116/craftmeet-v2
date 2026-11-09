package live.craftmeet.authentication.model;

import jakarta.persistence.*;
import live.craftmeet.persistence.BaseEntity;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a refresh token used to support user authentication.
 * This class is mapped to the refresh_tokens table and extends BaseEntity
 * to inherit auditing metadata such as creation and modification timestamps.
 * A refresh token is associated with a User and records a unique token value
 * together with its expiration instant, which determines whether the token
 * remains valid for authentication purposes.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private UUID jti;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
