package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    TokenService tokenService;

    @Spy
    HashMap<String, User> users;

    @InjectMocks
    UserService userService;

    @Captor
    ArgumentCaptor<String> usernameCaptor;

    @Test
    public void createUserTest() {
        final User user = userService
                .create("user1", "password1");
        verify(users, times(1)).put("user1", user);
        verify(users).containsKey(usernameCaptor.capture());
        assertEquals("user1", usernameCaptor.getValue());
    }

    @Test
    public void getUserTest() {
        User user = User.builder().username("fakeuser").password("password1").build();
        when(users.get("user1")).thenReturn(user);
        when(users.containsKey("user1")).thenReturn(true);
        final User fakeUser = userService.getUser("user1");
        assertEquals("password1", fakeUser.getPassword());
    }
}