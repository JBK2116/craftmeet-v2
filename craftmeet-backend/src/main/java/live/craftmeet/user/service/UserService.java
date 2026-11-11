package live.craftmeet.user.service;

import jakarta.transaction.Transactional;

import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.dto.UserResponse;
import live.craftmeet.user.exception.UserAlreadyExistsException;
import live.craftmeet.user.exception.UserInvalidCredentialsException;
import live.craftmeet.user.exception.UserNotFoundException;
import live.craftmeet.user.exception.UserNotVerifiedException;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for user account creation and credential/profile updates.
 * Coordinates user persistence through {@link UserRepository}, validates the
 * uniqueness of new account emails, maps persisted {@link User} entities to
 * {@link UserResponse} values, and translates persistence conflicts into
 * user-domain exceptions. Write operations are executed transactionally.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public UserService(UserRepository userRepository, CryptoService cryptoService) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    /**
     * Creates a new user account using the provided email, password, and username.
     * The password is encoded before persisting the account.
     * Verifies that no user already exists with the given email before persisting
     * the account, and translates a username persistence conflict into a
     * {@link UserAlreadyExistsException}.
     *
     * @param email    the email address for the new user account
     * @param password the raw password for the new user account
     * @param username the username for the new user account
     * @return the created user's account information
     */
    @Transactional
    public UserResponse createUser(String email, String password, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(cryptoService.hashPassword(password));
        user.setUsername(username);

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(username);
        }

        return UserResponse.from(user);
    }

    /**
     * Creates a new verified user account using the provided email and Google
     * identifier.
     * The account is marked as verified and the verification timestamp is set to
     * the current time.
     * If an account with the given email already exists, or persistence fails due
     * to a data integrity violation, a UserAlreadyExistsException is thrown.
     *
     * @param email    the email address for the new user account
     * @param googleId the Google identifier associated with the user account
     * @return the created user account represented as a UserResponse
     * @throws UserAlreadyExistsException if a user with the given email already
     *                                    exists
     */
    @Transactional
    public UserResponse createUserGoogle(String email, String googleId) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setGoogleId(googleId);
        user.setVerified(true);
        user.setVerifiedAt(Instant.now());

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(email);
        }

        return UserResponse.from(user);
    }

    /**
     * Updates the username of the user identified by the given user ID.
     *
     * @param userId   the unique identifier of the user whose username will be
     *                 updated
     * @param username the new username to assign to the user
     * @return the updated user's account information
     */
    @Transactional
    public UserResponse updateUsername(UUID userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setUsername(username);

        return UserResponse.from(user);
    }

    /**
     * Updates the password of the user identified by the given user ID.
     * The supplied password is encoded before being assigned to the user.
     *
     * @param userId         the unique identifier of the user whose password will
     *                       be updated
     * @param hashedPassword the new password to encode and assign to the user
     * @return the updated user's account information
     * @throws UserNotFoundException if no user exists with the given user ID
     */
    @Transactional
    public UserResponse updatePassword(UUID userId, String hashedPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setPassword(cryptoService.hashPassword(hashedPassword));

        return UserResponse.from(user);
    }

    /**
     * Authenticates a user by matching the provided password against the stored
     * password for the account identified by the given email. The account must
     * also be verified to authenticate successfully.
     *
     * @param email    the email address of the user account to authenticate
     * @param password the raw password to verify against the stored password
     * @return the authenticated user's account information
     * @throws UserNotFoundException           if no user exists with the given
     *                                         email
     * @throws UserInvalidCredentialsException if the password does not match
     * @throws UserNotVerifiedException        if the user account has not been
     *                                         verified
     */
    public UserResponse authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        if (!cryptoService.checkPassword(password, user.getPassword())) {
            throw new UserInvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException(user.getEmail());
        }

        return UserResponse.from(user);
    }
}
