package live.craftmeet.user.repository;

import live.craftmeet.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Provides persistence operations for users through Spring Data JPA,
 * including CRUD, pagination, and derived query methods for locating users by
 * email address, username, or Google account identifier. The repository is
 * keyed by the user's UUID identifier.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByGoogleId(String googleId);

    void deleteByEmail(String email);

    void deleteByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
