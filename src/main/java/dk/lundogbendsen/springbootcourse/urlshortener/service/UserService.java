package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.UserExistsException;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    private HashMap<String, User> users = new HashMap<>();

    @Autowired
    PasswordEncoder passwordEncoder;
    public User create(String userName, String password, List<String> roles) {
        if (users.containsKey(userName)) {
            throw new UserExistsException();
        }
        final User user = User.builder().username(userName).password(passwordEncoder.encode(password)).roles(roles).build();
        users.put(userName, user);
        return user;
    }

    public void delete() {
        SecurityContext context = SecurityContextHolder.getContext();
        User user  = (User) context.getAuthentication().getPrincipal();
        users.remove(user.getUsername());
        tokenService.deleteTokens();
        SecurityContextHolder.clearContext();
    }

    public User getUser(String userName) {
        return users.get(userName);
    }
}
