package live.craftmeet.authentication.service;

import live.craftmeet.authentication.model.ResetPasswordToken;
import live.craftmeet.authentication.repository.ResetPasswordTokenRepository;
import live.craftmeet.authentication.token.CooldownTokenException;
import live.craftmeet.authentication.token.InvalidTokenException;
import live.craftmeet.crypto.CryptoService;
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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTokenServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_TOKEN = "raw-token";
    private static final String TOKEN_HASH = "token-hash";
    private static final String TOKEN_SECRET = "secret";
    private static final long EXPIRATION_MS = 1_800_000L;
    private static final long COOLDOWN_MS = 900_000L;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CryptoService cryptoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResetPasswordTokenRepository resetPasswordTokenRepository;

    private ResetPasswordTokenService resetPasswordTokenService;

    @BeforeEach
    void setUp() {
        resetPasswordTokenService = new ResetPasswordTokenService(cryptoService, userRepository,
                resetPasswordTokenRepository, EXPIRATION_MS, COOLDOWN_MS, TOKEN_SECRET);
    }

    @Test
    void generate_createsAndSavesTokenForVerifiedUserWithoutExistingToken() {
        User user = user(true);
        when(user.getId()).thenReturn(USER_ID);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(resetPasswordTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cryptoService.generateRawToken()).thenReturn(RAW_TOKEN);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);

        String result = resetPasswordTokenService.generate(EMAIL);

        assertThat(result).isEqualTo(RAW_TOKEN);
        ArgumentCaptor<ResetPasswordToken> captor = ArgumentCaptor.forClass(ResetPasswordToken.class);
        verify(resetPasswordTokenRepository).save(captor.capture());
        ResetPasswordToken saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(saved.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void generate_rejectsUnknownUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordTokenService.generate(EMAIL))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(cryptoService, resetPasswordTokenRepository);
    }

    @Test
    void generate_rejectsUnverifiedUser() {
        User user = user(false);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> resetPasswordTokenService.generate(EMAIL))
                .isInstanceOf(UserNotVerifiedException.class);

        verifyNoInteractions(cryptoService, resetPasswordTokenRepository);
    }

    @Test
    void generate_rejectsExistingTokenDuringCooldown() {
        User user = user(true);
        when(user.getId()).thenReturn(USER_ID);
        ResetPasswordToken existingToken = mock(ResetPasswordToken.class);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(resetPasswordTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingToken));
        when(existingToken.getCreatedAt()).thenReturn(Instant.now().minusMillis(COOLDOWN_MS / 2));

        assertThatThrownBy(() -> resetPasswordTokenService.generate(EMAIL))
                .isInstanceOf(CooldownTokenException.class);

        verifyNoInteractions(cryptoService);
    }

    @Test
    void generate_replacesExistingTokenAfterCooldown() {
        User user = user(true);
        when(user.getId()).thenReturn(USER_ID);
        ResetPasswordToken existingToken = mock(ResetPasswordToken.class);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(resetPasswordTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingToken));
        when(existingToken.getCreatedAt()).thenReturn(Instant.now().minusMillis(COOLDOWN_MS + 1));
        when(cryptoService.generateRawToken()).thenReturn(RAW_TOKEN);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);

        String result = resetPasswordTokenService.generate(EMAIL);

        assertThat(result).isEqualTo(RAW_TOKEN);
        verify(resetPasswordTokenRepository).delete(existingToken);
        verify(resetPasswordTokenRepository).save(any(ResetPasswordToken.class));
    }

    @Test
    void validate_acceptsValidToken() {
        User user = user(true);
        ResetPasswordToken token = mock(ResetPasswordToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(resetPasswordTokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(token.getExpiresAt()).thenReturn(Instant.now().plusMillis(EXPIRATION_MS));

        assertThatCode(() -> resetPasswordTokenService.validate(RAW_TOKEN))
                .doesNotThrowAnyException();

        verify(userRepository, never()).save(any());
    }

    @Test
    void validate_rejectsUnknownToken() {
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(resetPasswordTokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);

        verifyNoInteractions(userRepository);
    }

    @Test
    void validate_rejectsTokenWhenUserDoesNotExist() {
        ResetPasswordToken token = mock(ResetPasswordToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(resetPasswordTokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validate_rejectsUnverifiedUser() {
        User user = user(false);
        ResetPasswordToken token = mock(ResetPasswordToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(resetPasswordTokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> resetPasswordTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validate_rejectsExpiredToken() {
        User user = user(true);
        ResetPasswordToken token = mock(ResetPasswordToken.class);
        when(cryptoService.hashToken(RAW_TOKEN, TOKEN_SECRET)).thenReturn(TOKEN_HASH);
        when(resetPasswordTokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(token.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(token.getExpiresAt()).thenReturn(Instant.now().minusMillis(1));

        assertThatThrownBy(() -> resetPasswordTokenService.validate(RAW_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    private User user(boolean verified) {
        User user = mock(User.class);
        when(user.isVerified()).thenReturn(verified);
        return user;
    }
}
