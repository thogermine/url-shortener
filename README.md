# Exercises for testing the persistence layer in URLShortener.

At this point we have a project implementing the business logic of the URLShortener. I uses MySql for persisting its 
data and therefore data will remain through restarts of the service.

Now it is time for testing the Persistence Layer.

When testing the Persistence Layer, we don't actually want to test if Spring Repositories actually produces correct SQL.

Instead we want to make certain that the methods WE provide on the Repository Interfaces, and any custom sql that WE 
have produced, actually works.

Looking at the TokenRepository we have the following custom methods:

```java
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByUser(User user);
    Optional<Token> findByTokenAndUser(String token, User user);
    void deleteAllByUser(User user);
    void deleteByTokenAndUser(String theToken, User user);
    Optional<Token> findByTokenAndProtectToken(String theToken, String protectToken);
}
```

In java you can add any method on an interface. We should test that the methods we add to a repository
follow the conventions of Spring. Otherwise nothing happens when we call that method.