package live.craftmeet.user.service;

import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.dto.UserResponse;
import live.craftmeet.user.exception.UserAlreadyExistsException;
import live.craftmeet.user.exception.UserInvalidCredentialsException;
import live.craftmeet.user.exception.UserNotFoundException;
import live.craftmeet.user.exception.UserNotVerifiedException;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService}, using mocked repository and crypto service.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String USERNAME = "username";
    private static final String RAW_PASSWORD = "rawPassword123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String GOOGLE_ID = "google-id-123";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, cryptoService);
    }

    private User user(String email, String username, String password, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setVerified(verified);
        return user;
    }

    @Test
    void createUser_encodesPasswordAndSaves() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(cryptoService.hashPassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.createUser(EMAIL, RAW_PASSWORD, USERNAME);

        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.username()).isEqualTo(USERNAME);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getPassword()).isEqualTo(ENCODED_PASSWORD);

        verify(cryptoService).hashPassword(RAW_PASSWORD);
    }

    @Test
    void createUser_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(EMAIL, RAW_PASSWORD, USERNAME))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void createUser_throwsWhenUsernameAlreadyTaken() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(cryptoService.hashPassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate username"));

        assertThatThrownBy(() -> userService.createUser(EMAIL, RAW_PASSWORD, USERNAME))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void createUserGoogle_createsVerifiedUser() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.createUserGoogle(EMAIL, GOOGLE_ID);

        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.verified()).isTrue();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getGoogleId()).isEqualTo(GOOGLE_ID);
        assertThat(saved.isVerified()).isTrue();
        assertThat(saved.getVerifiedAt()).isNotNull();
    }

    @Test
    void createUserGoogle_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUserGoogle(EMAIL, GOOGLE_ID))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void updateUsername_updatesAndReturnsResponse() {
        User existing = user(EMAIL, "oldUsername", ENCODED_PASSWORD, true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existing));

        UserResponse response = userService.updateUsername(USER_ID, "newUsername");

        assertThat(response.username()).isEqualTo("newUsername");
        assertThat(existing.getUsername()).isEqualTo("newUsername");
    }

    @Test
    void updateUsername_throwsWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUsername(USER_ID, "newUsername"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updatePassword_encodesAndUpdates() {
        User existing = user(EMAIL, USERNAME, "oldEncoded", true);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(cryptoService.hashPassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        UserResponse response = userService.updatePassword(EMAIL, RAW_PASSWORD);

        assertThat(existing.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(response.email()).isEqualTo(EMAIL);
        verify(cryptoService).hashPassword(RAW_PASSWORD);
    }

    @Test
    void updatePassword_throwsWhenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void authenticate_returnsResponseWhenCredentialsValid() {
        User existing = user(EMAIL, USERNAME, ENCODED_PASSWORD, true);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(cryptoService.checkPassword(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        UserResponse response = userService.authenticate(EMAIL, RAW_PASSWORD);

        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.verified()).isTrue();
    }

    @Test
    void authenticate_throwsWhenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void authenticate_throwsWhenPasswordDoesNotMatch() {
        User existing = user(EMAIL, USERNAME, ENCODED_PASSWORD, true);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(cryptoService.checkPassword(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticate(EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserInvalidCredentialsException.class);
    }

    @Test
    void authenticate_throwsWhenUserNotVerified() {
        User existing = user(EMAIL, USERNAME, ENCODED_PASSWORD, false);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(cryptoService.checkPassword(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThatThrownBy(() -> userService.authenticate(EMAIL, RAW_PASSWORD))
                .isInstanceOf(UserNotVerifiedException.class);
    }
}
