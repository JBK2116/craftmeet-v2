package live.craftmeet.user.model;

import jakarta.persistence.*;
import live.craftmeet.persistence.BaseEntity;
import live.craftmeet.user.utils.Constants;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing an application user.
 * This class is mapped to the users table and extends BaseEntity to inherit
 * auditing metadata such as creation and modification timestamps. It serves as
 * the persistent model for user account data and authentication-related
 * information.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(unique = true, length = Constants.MAX_GOOGLE_ID_LENGTH)
    private String googleId;

    @Column(unique = true, length = Constants.MAX_USERNAME_LENGTH)
    private String username;

    @Column(nullable = false, unique = true, length = Constants.MAX_EMAIL_LENGTH)
    private String email;

    private String password;

    @Column(nullable = false)
    private boolean verified = false;

    private Instant verifiedAt;
}