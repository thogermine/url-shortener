package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    private HashMap<String, User> users = new HashMap<>();

    public User create(String userName, String password) {
        if (users.containsKey(userName)) {
            throw new UserExistsException();
        }
        final User user = User.builder().username(userName).password(password).build();
        users.put(userName, user);
        return user;
    }

    public void delete(String userName) {
        final User user = users.get(userName);
        if (user != null) {
            users.remove(userName);
            tokenService.deleteTokens(user);
        }
    }

    public User getUser(String userName) {
        return users.get(userName);
    }
}
