package live.craftmeet.authentication.service;

import live.craftmeet.authentication.model.VerifyEmailToken;
import live.craftmeet.authentication.repository.VerifyEmailTokenRepository;
import live.craftmeet.authentication.token.CooldownTokenException;
import live.craftmeet.authentication.token.InvalidTokenException;
import live.craftmeet.crypto.CryptoService;
import live.craftmeet.user.exception.UserAlreadyVerifiedException;
import live.craftmeet.user.exception.UserNotFoundException;
import live.craftmeet.user.model.User;
import live.craftmeet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyEmailTokenServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_TOKEN = "raw-token";
    private static final String TOKEN_HASH = "token-hash";
    private static final String TOKEN_SECRET = "secret";
    private static final long EXPIRATION_MS = 86_400_000L;
    private static final long COOLDOWN_MS = 900_000L;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CryptoService cryptoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerifyEmailTokenRepository verifyEmailRepository;

    private VerifyEmailTokenService verifyEmailTokenService;

    @BeforeEach
    void setUp() {
        verifyEmailTokenService = new VerifyEmailTokenService(cryptoService, userRepository,
                verifyEmailRepository, EXPIRATION_MS, COOLDOWN_MS, TOKEN_SECRET);
    }

    @Test
    void generate_createsAndSavesTokenForUnverifiedUserWithoutExistingToken() {
        User user = user(false);
        when(user.getId()).thenReturn(USER_ID);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(verifyEmailRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cryptoService.generateRawToken()).thenReturn(RAW_TOKEN);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);

        String result = verifyEmailTokenService.generate(EMAIL);

        assertThat(result).isEqualTo(RAW_TOKEN);
        ArgumentCaptor<VerifyEmailToken> captor = ArgumentCaptor.forClass(VerifyEmailToken.class);
        verify(verifyEmailRepository).save(captor.capture());
        VerifyEmailToken saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(saved.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void generate_rejectsUnknownUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyEmailTokenService.generate(EMAIL))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(cryptoService, verifyEmailRepository);
    }

    @Test
    void generate_rejectsAlreadyVerifiedUser() {
        User user = user(true);
        when(user.getEmail()).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> verifyEmailTokenService.generate(EMAIL))
                .isInstanceOf(UserAlreadyVerifiedException.class);

        verifyNoInteractions(cryptoService, verifyEmailRepository);
    }

    @Test
    void generate_rejectsExistingTokenDuringCooldown() {
        User user = user(false);
        when(user.getId()).thenReturn(USER_ID);
        VerifyEmailToken existingToken = mock(VerifyEmailToken.class);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(verifyEmailRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingToken));
        when(existingToken.getCreatedAt()).thenReturn(Instant.now().minusMillis(COOLDOWN_MS / 2));

        assertThatThrownBy(() -> verifyEmailTokenService.generate(EMAIL))
                .isInstanceOf(CooldownTokenException.class);

        verifyNoInteractions(cryptoService);
    }

    @Test
    void generate_replacesExistingTokenAfterCooldown() {
        User user = user(false);
        when(user.getId()).thenReturn(USER_ID);
        VerifyEmailToken existingToken = mock(VerifyEmailToken.class);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(verifyEmailRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingToken));
        when(existingToken.getCreatedAt()).thenReturn(Instant.now().minusMillis(COOLDOWN_MS + 1));
        when(cryptoService.generateRawToken()).thenReturn(RAW_TOKEN);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);

        String result = verifyEmailTokenService.generate(EMAIL);

        assertThat(result).isEqualTo(RAW_TOKEN);
        verify(verifyEmailRepository).delete(existingToken);
        verify(verifyEmailRepository).save(any(VerifyEmailToken.class));
    }

    @Test
    void validate_marksUserVerifiedForValidToken() {
        User user = user(false);
        VerifyEmailToken token = mock(VerifyEmailToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(verifyEmailRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getExpiresAt()).thenReturn(Instant.now().plusMillis(EXPIRATION_MS));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        verifyEmailTokenService.validate(RAW_TOKEN);

        verify(user).setVerified(true);
        verify(user).setVerifiedAt(any(Instant.class));
        verify(userRepository).save(user);
    }

    @Test
    void validate_rejectsUnknownToken() {
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(verifyEmailRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyEmailTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);

        verifyNoInteractions(userRepository);
    }

    @Test
    void validate_rejectsExpiredToken() {
        VerifyEmailToken token = mock(VerifyEmailToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(verifyEmailRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getExpiresAt()).thenReturn(Instant.now().minusMillis(1));

        assertThatThrownBy(() -> verifyEmailTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);

        verifyNoInteractions(userRepository);
    }

    @Test
    void validate_rejectsTokenWhenUserDoesNotExist() {
        VerifyEmailToken token = mock(VerifyEmailToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(verifyEmailRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getExpiresAt()).thenReturn(Instant.now().plusMillis(EXPIRATION_MS));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyEmailTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validate_rejectsAlreadyVerifiedUser() {
        User user = user(true);
        when(user.getEmail()).thenReturn(EMAIL);
        VerifyEmailToken token = mock(VerifyEmailToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(verifyEmailRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getExpiresAt()).thenReturn(Instant.now().plusMillis(EXPIRATION_MS));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> verifyEmailTokenService.validate(RAW_TOKEN))
                .isInstanceOf(UserAlreadyVerifiedException.class);

        verify(userRepository).findById(USER_ID);
    }

    private User user(boolean verified) {
        User user = mock(User.class);
        when(user.isVerified()).thenReturn(verified);
        return user;
    }
}
