package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    TokenService tokenService;

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    public void createUserTest() {
        final User user = userService.create("user1", "password1");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getUserTest() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(User.builder().username("fakeuser").password("password1").build()));
        final User fakeUser = userService.getUser("user1");
        assertEquals("password1", fakeUser.getPassword());
    }
}