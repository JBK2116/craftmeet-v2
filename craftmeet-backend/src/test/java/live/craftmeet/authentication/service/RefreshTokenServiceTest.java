package live.craftmeet.authentication.service;

import live.craftmeet.authentication.model.RefreshToken;
import live.craftmeet.authentication.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
    }

    @Test
    void create_clearsExistingTokensAndPersistsNewJti() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID result = refreshTokenService.create(USER_ID);

        assertThat(result).isNotNull();
        verify(refreshTokenRepository).deleteByUserId(USER_ID);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(captor.getValue().getJti()).isEqualTo(result);
    }

    @Test
    void clear_deletesAllTokensForUser() {
        refreshTokenService.clear(USER_ID);

        verify(refreshTokenRepository).deleteByUserId(USER_ID);
    }

    @Test
    void rotate_returnsNumberOfUpdatedTokens() {
        UUID oldJti = UUID.randomUUID();
        UUID newJti = UUID.randomUUID();
        when(refreshTokenRepository.rotate(USER_ID, oldJti, newJti)).thenReturn(1);

        int result = refreshTokenService.rotate(USER_ID, oldJti, newJti);

        assertThat(result).isEqualTo(1);
        verify(refreshTokenRepository).rotate(USER_ID, oldJti, newJti);
    }
}
