package dk.lundogbendsen.springbootcourse.urlshortener.repositories;

import dk.lundogbendsen.springbootcourse.urlshortener.model.Token;
import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByUser(User user);
    Optional<Token> findByTokenAndUser(String token, User user);

    void deleteAllByUser(User user);

    void deleteByTokenAndUser(String theToken, User user);

    Optional<Token> findByTokenAndProtectToken(String theToken, String protectToken);
}
