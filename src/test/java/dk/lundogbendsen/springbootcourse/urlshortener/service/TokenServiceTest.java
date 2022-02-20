package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.Token;
import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.repositories.TokenRepository;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    TokenRepository tokenRepository;
    @InjectMocks
    TokenService tokenService;
    private User user = User.builder().username("username").password("password").build();

    @Test
    @DisplayName("create token with the name 'token' (fails)")
    public void testCreateTokenWithTheNameToken() {
        assertThrows(IllegalTokenNameException.class, () -> tokenService.create("token", "https://dr.dk", null, user));
    }

    @Test
    @DisplayName("create token that already exists (fails)")
    public void testCreateTokenThatAlreadExists() {
        when(tokenRepository.findById("token1")).thenReturn(Optional.ofNullable(Token.builder().build()));
        assertThrows(TokenAlreadyExistsException.class, () -> tokenService.create("token1", "https://dr.dk", null, user));
    }

    @Test
    @DisplayName("create token without a targetUrl (fails)")
    public void testCreateTokenWithoutTargetUrl() {
            assertThrows(TokenTargetUrlIsNullException.class, () -> tokenService.create("token1", null, null, user));
    }

    @Test
    @DisplayName("create token with an invalid targetUrl (fails)")
    public void testCreateTokenWithInvalidTargetUrl() {
        assertThrows(InvalidTargetUrlException.class, () -> tokenService.create("token1", "htt", null, user));
    }

    @Test
    @DisplayName("create token with a targetUrl containing localhost (fails)")
    public void testCreateTokenWithTargetUrlContainingLocalhost() {
        assertThrows(IllegalTargetUrlException.class, () -> tokenService.create("token1", "http://localhost:8080/abc", null, user));
    }

    @Test
    @DisplayName("create token with a legal targetUrl (success)")
    public void testCreateTokenWithLocalTargetUrl() {
        tokenService.create("abc", "https://dr.dk", "pt1", user);
    }
}