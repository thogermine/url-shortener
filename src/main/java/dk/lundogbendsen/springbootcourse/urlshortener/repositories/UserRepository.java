package dk.lundogbendsen.springbootcourse.urlshortener.repositories;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
