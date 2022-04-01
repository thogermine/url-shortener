# Exercises for build an API on Url-shortener

At this point, the service layer does not have true persistence. When the application shuts down, all data are gone.

These exercises will bring persistence to H2 Embedded Database.

It is possible to connect to the H2 database using a browser if the property

`spring.h2.console.enabled=true`

Hit the console in a browser: `localhost:8080/h2-console`

Also we want to exploit the devtools to avoid constant restart of application while we develop.

### Exercise 1: Add data jpa and h2 connector

- Add to pom.xml two dependencies:
    1. the data-jpa springboot starter (gives us Repositories and JPA annotations)
    2. the H2 dependency (Adds a H2 datasource).
    3. the spring-boot-devtools dependency
    4. the web starter dependency (for accessing h2 console)
- Add the following properties to use H2:
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```
Hint: you can use a shortcut in the pom for making Intellij find the dependency: ctrl-n (or right-click and "generate")

#### Solution
pom.xml:
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
```


### Exercise 2: Change the Domain model to an Entity Model
- Add JPA annotations to the Token and User classes thereby turning them into Entities.
- Add @Entity on both classes
- Add a field `Long id` to the Token class and annotate it as the Id.
- Use the username as an id by annotating it as the Id.

#### Solution
Token:
```java
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    String token;
    String protectToken;
    String targetUrl;
    @ManyToOne
    User user;
}
```

User:
```java
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    String username;
    String password;
}
```


### Exercise 3: Create Repositories
Repositories are access point to the database. We need one per Entity.

- Create a class `UserRepository` for the User entity.
- Create a class `TokenRepository` for the Token entity.

#### Solution
UserRepository:
```java
public interface UserRepository extends JpaRepository<User, String> {
}
```

TokenRepository:
```java
public interface TokenRepository extends JpaRepository<Token, String> {
}
```



### Exercise 4: Refactor UserService to use a Repository
It is easy to just delete the field users and then fix the compilation errors by added the UserRepository and the update the code.
- Add UserRepository as a dependency.
- Delete the field users. This makes the compiler complain.
- Update all the methods to use repository for CRUD operations on User.

#### Solution

UserService:
```java
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
```


### Exercise 5: Refactor TokenService to use a Repository
It is easy to just delete the field tokens and then fix the compilation errors by added the TokenRepository and the update the code.
- Add TokenRepository as a dependency.
- Delete the field tokens. This makes the compiler complain.
- Update all the methods to use repository for CRUD operations on Token.

#### Solution
TokenService:
```java
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

```
