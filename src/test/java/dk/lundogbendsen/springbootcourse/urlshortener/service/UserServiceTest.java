package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    TokenService tokenService;
    @InjectMocks
    UserService userService;

    @Test
    public void createUserTest() {
        final User user = userService.create("user1", "password1");
        final User getUser = userService.getUser("user1");
        assertEquals("password1", getUser.getPassword());
    }
}