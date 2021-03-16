package dk.lundogbendsen.springbootcourse.urlshortener.service;

import dk.lundogbendsen.springbootcourse.urlshortener.model.Token;
import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import dk.lundogbendsen.springbootcourse.urlshortener.repositories.TokenRepository;
import dk.lundogbendsen.springbootcourse.urlshortener.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    TokenRepository tokenRepository;



    public List<Token> listUserTokens(User user) {
        if (user == null) {
            throw new AccessDeniedException();
        }
        final List<Token> userTokens = tokenRepository.findAllByUser(user);
        return userTokens;
    }

    public void deleteTokens(User user) {
        if (user == null) {
            throw new AccessDeniedException();
        }
        tokenRepository.deleteAllByUser(user);
    }

    public Token create(String theToken, String targetUrl, String protectToken, User user) {
        if (user == null) {
            throw new AccessDeniedException();
        }
        if (theToken.equals("token")) {
            throw new IllegalTokenNameException();
        }
        Optional<Token> existingToken = tokenRepository.findById(theToken);
        if (existingToken.isPresent()) {
            throw new TokenAlreadyExistsException();
        }
        if (targetUrl == null) {
            throw new TokenTargetUrlIsNullException();
        }
        if (targetUrl.contains("localhost")) {
            throw new IllegalTargetUrlException();
        }
        try {
            new URL(targetUrl);
        } catch (MalformedURLException e) {
            throw new InvalidTargetUrlException();
        }

        final Token token = Token.builder().token(theToken).targetUrl(targetUrl).protectToken(protectToken).user(user).build();
        tokenRepository.save(token);
        return token;
    }

    public Token update(String theToken, String targetUrl, String protectToken, User user) {
        if (user == null) {
            throw new AccessDeniedException();
        }

        final Optional<Token> tokenOptional = tokenRepository.findByTokenAndUser(theToken, user);
        if (!tokenOptional.isPresent()) {
            throw new TokenNotFoundException();
        }

        final Token token = tokenOptional.get();
        if (targetUrl == null) {
            targetUrl = token.getTargetUrl();
        }
        if (targetUrl.contains("localhost")) {
            throw new IllegalTargetUrlException();
        }
        try {
            new URI(targetUrl);
        } catch (URISyntaxException e) {
            throw new InvalidTargetUrlException();
        }

        token.setTargetUrl(targetUrl);
        token.setProtectToken(protectToken);
        return tokenRepository.save(token);
    }

    public void deleteToken(String theToken, User user) {
        tokenRepository.deleteByUser(theToken, user);
    }

    public String resolveToken(String theToken, String protectToken) {
        final Optional<Token> token = tokenRepository.findByTokenAndProtectToken(theToken, protectToken);
        if (!token.isPresent()) {
            throw new TokenNotFoundException();
        }
        return token.get().getTargetUrl();
    }

    public Token getToken(String theToken, User user) {
        final Optional<Token> token = tokenRepository.findByTokenAndUser(theToken, user);
        if (!token.isPresent()) {
            throw new TokenNotFoundException();
        }
        return token.get();
    }
}
