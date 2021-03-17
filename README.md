# Use Case: URL Shortener
We have been tasked with producing a URL shortener service.

A URL shortener basically converts a long and cumbersome URL to a short, human-readable token which is easier to remember.

Examples on the internet are **bit.ly** and **TinyUrl**.

Examples:

http://localhost:8080/company-spreadsheet -> https://docs.google.com/document/d/VOLbjBNXbT5Z3M8oDsMVhDZnkHY2oDeDwLp8cssnN8/edit#


#### Concepts
- token: the unique, human-readable/memorable text that points to some URL, must not be the word "token", must be unique.
- targetUrl: the url a token is associated with. Must be a valid URI, must not be a URL on the Url Shortener service it self.
- protectToken: an api-token that is associated with a token and must be provided as a http header in order to resolve a token.
- user: the owner and controller of the token.
- token-collision: when a token is being registered more than once.
- host: the server running the url-shortener service. This is probably http://localhost
- redirect: the technique to make the browser go to a different site than written in the URL.


#### Features
Token actions:
- All actions on tokens must be authenticated.
- Register token (Prevent token-collisions).
- Test target URL. (is the target browser-redirectable, is it called "token")
- Delete token.
- Update URL by token.
- A token is owned and controlled by a user.
- Protect token (make url-shortening accessible to authenticated users)
- Circle detection (Prevent loops when tokens points to the URL shortener it self)

User actions:
- Create user (onboard a new user with username/password. Username is unique)
- Delete user (also delete all user tokens)

Follow actions:
- The all important feature of the service is retrieving a redirect from a token




### Step 1: Create a naked Springboot project
Include Lombok if you want to use that.

### Step 2: Create the Domain Model
The Domain model represents the business objects. In this case it is a User and a Token. These should be two classes with properties representing the business objects.

- Create a User model with username and password properties.
- Create a Token model with token, targetUrl, protectToken and User properties.

#### Solution
User model:
```java
@Data
@Builder
public class User {
  String username;
  String password;
}
```

Token Model:
```java
@Data
@Builder
public class Token {
    String token;
    String protectToken;
    String targetUrl;
    User user;
}
```





### Step 2: Create a TokenService for handling Tokens
This service implements the business operations required for handling Tokens.
The service should have the following operations:

- `public Token create(String theToken, String targetUrl, String protectToken, User user)`
  - collision detection
  - targetUrl validation (legal URL? Circle?)
- `public List<Token> listUserTokens(User user)`
  - ownership check
- `public void deleteTokens(User user)`
  - ownership check
- `public Token update(String theToken, String targetUrl, String protectToken, User user)`
  - collision detection
  - targetUrl validation (legal URL? Circle?)
  - ownership check
- `public void deleteToken(String theToken, String userName)`
  - ownership check
- `public Token getToken(String theToken, String username)`
  - ownership check
- `public String resolveToken(String theToken, String protectToken)`
  - protected check

There are a few constraint when working with Tokens (see the features description above). If a Constraint is violated, an appropriate exception must be thrown. It is recommended - but not required - that the business exceptions are RuntimeExceptions.
The TokenService uses an in-memory datastore (a HashMap).
No Token can be named "token" as it would conflict with the redirection/follow semantics.

#### Solution

TokenService:
```java
@Service
public class TokenService {
  private HashMap<String, Token> tokens = new HashMap<>();


  public List<Token> listUserTokens(User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    final List<Token> userTokens = this.tokens.values().stream().filter(token -> token.getUser().getUsername().equals(user.getUsername())).collect(Collectors.toUnmodifiableList());
    return userTokens;
  }

  public void deleteTokens(User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    tokens.values().removeIf(token -> token.getUser().getUsername().equals(user.getUsername()));
  }

  public Token create(String theToken, String targetUrl, String protectToken, User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    if (theToken.equals("token")) {
      throw new IllegalTokenNameException();
    }
    if (tokens.containsKey(theToken)) {
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
    tokens.put(theToken, token);
    return token;
  }

  public Token update(String theToken, String targetUrl, String protectToken, User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    final Token token = tokens.get(theToken);
    if (token == null) {
      throw new TokenNotFoundException();
    }
    if (!token.getUser().getUsername().equals(user.getUsername())) {
      throw new AccessDeniedException();
    }
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
    return token;
  }

  public void deleteToken(String theToken, User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    final Token token = tokens.get(theToken);
    if (!token.getUser().getUsername().equals(user.getUsername())) {
      throw new AccessDeniedException();
    }
    tokens.remove(theToken);
  }

  public String resolveToken(String theToken, String protectToken) {
    final Token token = tokens.get(theToken);
    if (token == null) {
      throw new TokenNotFoundException();
    }
    if (token.getProtectToken() != null && !token.getProtectToken().equals(protectToken)) {
      throw new AccessDeniedException();
    }

    return token.getTargetUrl();
  }

  public Token getToken(String theToken, User user) {
    if (user == null) {
      throw new AccessDeniedException();
    }
    final Token token = tokens.get(theToken);
    if (!token.getUser().getUsername().equals(user.getUsername())) {
      throw new AccessDeniedException();
    }
    return token;
  }
}
```

Exceptions:

```java
AccessDeniedException.java
IllegalTargetUrlException.java
IllegalTokenNameException.java
InvalidTargetUrlException.java
TokenAlreadyExistsException.java
TokenNotFoundException.java
TokenTargetUrlIsNullException.java
UserExistsException.java
UserNotFoundException.java
```


### Step 3: Create a UserService for handling Users
- Create a Service `UserService` with the following methods:
  - `public User create(String userName, String password)`
  - `public void delete(String userName)`
  - `public User getUser(String userName)`

#### Solution
UserService:
```java
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
    if (!users.containsKey(userName)) {
      throw new UserNotFoundException();
    }
    return users.get(userName);
  }
}
```

