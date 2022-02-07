package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.IllegalTargetUrlException;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.IllegalTokenNameException;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.InvalidTargetUrlException;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.TokenTargetUrlIsNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
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
        tokenService.create("token1", "https://dr.dk", null, user);
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