package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.model.Token;
import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.service.TokenService;
import dk.lundogbendsen.springbootcourse.urlshortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class TokenController {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Token> list(@RequestHeader String username) {
        final User user = userService.getUser(username);
        return tokenService.listUserTokens(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final String token = body.get("token");
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        tokenService.create(token, targetUrl, protectToken, user);
    }

    @PutMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String token, @RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        tokenService.update(token, targetUrl, protectToken, user);
    }

    @DeleteMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String token, @RequestHeader String username) {
        tokenService.deleteToken(token, username);
    }

    @PutMapping("/{token}/protect")
    @ResponseStatus(HttpStatus.CREATED)
    public void protect(@PathVariable("token") String theToken, @RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final Token token = tokenService.getToken(theToken, username);
        String protectToken = body.get("protectToken");
        tokenService.update(theToken, token.getTargetUrl(), protectToken, user);
    }
}
