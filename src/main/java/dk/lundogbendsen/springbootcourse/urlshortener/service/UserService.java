package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.repositories.UserRepository;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.UserExistsException;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;

    public User create(String userName, String password) {
        if (userRepository.existsById(userName)) {
            throw new UserExistsException();
        }
        final User user = User.builder().username(userName).password(password).build();
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void delete(String userName) {
        final Optional<User> user = userRepository.findById(userName);
        if (user.isPresent()) {
            tokenService.deleteTokens(user.get());
            userRepository.delete(user.get());
        }
    }

    public User getUser(String userName) {
        return userRepository.findById(userName).orElseThrow(UserNotFoundException::new);
    }
}
