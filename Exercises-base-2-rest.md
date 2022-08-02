# Exercises for build an API on Url-shortener

The base project includes a domain model and a service layer. It is time to add an API on top of that.


### Exercise 1: Make the project a Springboot web project
- Include the starter-web in the pom.xml.
- Goto Application class in the namespace root and annotate it with @SpringBootApplication.
- Also fill out the main method to build a Spring Application Context
  - Hint: `SpringApplication.run(ApiApplication.class, args);`
- Run the main, and verify that it starts a webserver.

#### Solution
pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Main class:
```java
package dk.lundogbendsen.springbootcourse.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);

  }
}
```

### Exercise 2: Expose the Service layer in an API.
Make an API that exposes each method in the service layer.

- Make a UserController with path prefix "/user" and the following operations:
  - createUser() (status code 201)
  - deleteUser() (status code 204)
  - getUser()

- Make a TokenController with path prefix "/token" and the following operations:
  - createToken() (status code 201)
  - getToken()
  - deleteToken() (status code 204)
  - listTokens()
  - updateToken()

- Make a TokenFollowController with path prefix "/" and the following operations:
  - followToken() (status code 301)

Beware of the Security constraints of the Service Layer: Token operations can only be performed by the User owning the tokens.
The Controller layer must therefore collect user information somehow along with the input to the Tokenoperations.


Special attention must be given to the followToken. This is where the whole business values of the service lies. The followToken operation must:
- send a status code 301.
- send a location header with the targetUrl of the token.
- the Token can be protected by a "protectToken", so you should support the client providing one _if required_.

#### Solution (3 controllers)

**src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/UserController.java**

```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.create(user.getUsername(), user.getPassword());
    }

    @GetMapping("{userName}")
    public User getUser(@PathVariable String userName) {
        return userService.getUser(userName);
    }

    @DeleteMapping("{userName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userName) {
        userService.delete(userName);
    }
}

```

**src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/TokenController.java**
```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller;


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
    public Token create(@RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final String token = body.get("token");
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        return tokenService.create(token, targetUrl, protectToken, user);
    }

    @PutMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Token update(@PathVariable String token, @RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        return tokenService.update(token, targetUrl, protectToken, user);
    }

    @DeleteMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String token, @RequestHeader String username) {
        tokenService.deleteToken(token, username);
    }

    @PutMapping("/{token}/protect")
    @ResponseStatus(HttpStatus.CREATED)
    public Token protect(@PathVariable("token") String theToken, @RequestBody Map<String, String> body, @RequestHeader String username) {
        final User user = userService.getUser(username);
        final Token token = tokenService.getToken(theToken, username);
        String protectToken = body.get("protectToken");
        return tokenService.update(theToken, token.getTargetUrl(), protectToken, user);
    }
}

```

**src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/FollowTokenController.java**

```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller;


@RestController
public class FollowTokenController {
    @Autowired
    TokenService tokenService;

    @GetMapping("{token}")
    public ResponseEntity<Object> follow(@PathVariable String token, @RequestHeader(required = false) String protectToken) {
        final String targetUrl = tokenService.resolveToken(token, protectToken);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, targetUrl).build();
    }

}

```

### Exercise 3: Exception handling for Service Layer
At this point we have a working API, but error handling could be better.

When the client violates the business rules, they get a 500 error without much information.

In this exercise we will upgrade the exception handling ny introducing a RestController advice that handles any service exception.

Inspecting the Exceptions from the business layer, we could categorize them into:
"Not found" (status code 404), "conflict" (status code 409), "validation" (status code 422), "security" (status code 401).


- Make a class ControllerAdvicerServiceLayer and annotate it with `@RestControllerAdvice`. This instructs Spring to look in this class for finding ExceptionHandlers.

- For each category you decide, make an exception handler that receives the exceptions in the category.
- The exception handler should explicitly denote the list of Exceptions that it handles in the @ExceptionHandler annotation.
- It must set an appropriate Status Code (using @ResponseStatus).
- It must return a Json object with a "message" key with an appropriate value.


Import the postman collection: `UrlShortener v1.postman_collection.json`

Use it to test that your solution works!


#### Solution
```java
@RestControllerAdvice
public class ControllerAdvicerServiceLayer {

    @ExceptionHandler({TokenAlreadyExistsException.class, UserExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(Exception exception) {
        if (exception instanceof TokenAlreadyExistsException) {
            return Map.of("message", "The token already exists");
        } else {
            return Map.of("message", "The user already exists");
        }
    }

    @ExceptionHandler({TokenNotFoundExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(TokenNotFoundExistsException exception) {
        return Map.of("message", "The token was not found");
    }

    @ExceptionHandler({IllegalTargetUrlException.class, IllegalTokenNameException.class, InvalidTargetUrlException.class, TokenTargetUrlIsNullException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleValidation(Exception exception) {
        return Map.of("message", "The token did not validate", "validation-type", exception.getClass().getSimpleName());
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleSecurity(AccessDeniedException exception, HttpServletRequest request) {
        return Map.of("message", "The operation is not allowed", "path", request.getRequestURI());
    }
}
```


### Exercise 4: Security into Intercepter
At the moment each webservice is required to handle issues of security explicitly. Normally you would expect this to be a cross-cutting concern,
and those types of concern belong in a Filter or Intercepter.

In this exercise we will refactor the controller layer by extracting security handling into an Intercepter.

We will also make use of more standardized approach of authentication, namely the Authentication mechanism:
- Basic Auth

Basic Auth requires the value of the `Authorization` header to follow this pattern: "Basic <digest>", where the <digest> is base64("<username>:<password>").

We will introduce an Intercepter that inspects the Authorization header and extract any authentication information found.
The authentication is then placed in a SecurityContext in a thread safe way (hint: ThreadLocal).

In this way, any method requiring authentication can consult the SecurityContext to get the required authentication - or fail if not available or invalid.

### Step 1: Create a SecurityContext to hold the credentials:

- Create a class SecurityContext
- Make a static field: `static final ThreadLocal<User> userState = new ThreadLocal<>();`

These fields will hold a reference to the User presented in Basic Auth, or the protectToken, if one was presented using Bearer Auth.

Add static getters and setters for both User and protectToken using the fields.

#### Solution step 1.
```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller.security;

import dk.lundogbendsen.springbootcourse.urlshortener.model.User;

public class SecurityContext {
  static final ThreadLocal<User> userState = new ThreadLocal<>();

  public static void setUser(User user) {
    userState.set(user);
  }

  public static User getUser() {
    return userState.get();
  }
}
```
`
### Step 2: Intercepter for extracting Basic Auth
Now we have a place to store credentials.

In this exercise we will make an Intercepter that extracts the User from the Basic Auth.

It should extract the value of the Authorization header and if it starts with the word "Basic", we are in business.

The it should extract the digest, decode it using base64, and split it on the colon char separating username and password.

The it should look up the user with UserService, and check that the passwords match.

If it does, it should set the User in the SecurityContext.

- Make a class `SecurityIntercepter` in a package `controller.security`.
- Autowire UserService into the class.
- Implement the `HandlerInterceptor` interface, override the `preHandle` method.
- Extract the header `Authorization`.
- Extract the value and check if it starts with the word "Basic".
- If so, continue extracting the digest staring on index 6 of the value.
- Base64 decode the digest.
- Split the decoded value by the char ":". Left side is username, right side is password.
- Use the userService to find the user.
- If none is found, throw an AccessDeniedException.
- Check that the users password matches the one from the digest.
- If not - throw an AccessDeniedException.
- Set the User in the SecurityContext.
- Return true.

We also need to clean up after the request is handled.

- Override the `postHandle()` method.
- set the user in the SecurityContext to null. This clears authentication from the SecurityContext so the thread can be used for the next request.


#### Solution step 2
```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller.security;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class SecurityIntercepter implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String auth = request.getHeader("Authorization");
        if (auth != null) {
            if (auth.startsWith("Basic")) {
                String userNamePassword = auth.substring(6);
                String decoded = new String(Base64.getDecoder().decode(userNamePassword));
                final String[] split = decoded.split(":");
                String userName = split[0];
                String password = split[1];
                final User user = userService.getUser(userName);
                if (user != null && user.getPassword().equals(password)) {
                    SecurityContext.setUser(user);
                } else {
                    throw new AccessDeniedException();
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        SecurityContext.setUser(null);
    }
}

```

### Step 3: Register the SecurityIntercepter
The Intercepter must now be registered to take effect.

- Make a new class `SecurityConfig` that implements the `WebMvcConfigurer` interface.
- Annotate it with `@Configuration`. This instructs Spring that there are Beans and configuration inside the class.
- Make method the makes a Bean out of the SecurityIntercepter.
- Override the `addInterceptors()` method.
- Use the registry to register the SecurityIntercepter. You can get a handle to the Bean by calling the method from before where you registered the intercepter.

#### Solution step 3
```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityIntercepter());
    }

    @Bean
    public SecurityIntercepter securityIntercepter() {
        return new SecurityIntercepter();
    }
}
```

### Step 4: Test the Intercepter
Now we will start the application in DEBUG mode and try it out.

- Start the application in debug mode.
- Set a break point in the beginning of the Intercepter.
- Execute the command: `curl localhost:8080/user -u "user:password"`.
  The -u option takes a cleartext string representing the username and password, digest it and use it for Basic Auth.

- Debug through your Intercepter and see that it correctly extracts the values for user and password.
  It will not find any user since you just restarted the application, and therefore it should result in an AccessDeniedException.


### Step 5: Refactor the Controller Layer
We now have a mechanism to extract User credentials from the request, and now we can let the Controller layer rely on that instead of extracting headers it self.

Looking at the TokenController we see that most operations require a username which is currently taken by `@RequestHeader String username`. These methods should be refactored to use the SecurityContext.

- Remove the `@RequestHeader String username` from all method signatures.
- Where the TokenService require a userName or a User, use the SecurityContext to get the User and use it directly or extract the username from the User instance.

Now it is time to test the new solution using Basic Auth.

Import the postman collection: `UrlShortener v2.postman_collection.json`

Use it to test that your solution works!

#### Solution step 5

```java
package dk.lundogbendsen.springbootcourse.urlshortener.controller;

import dk.lundogbendsen.springbootcourse.urlshortener.controller.security.SecurityContext;
import dk.lundogbendsen.springbootcourse.urlshortener.model.Token;
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
  public List<Token> list() {
    return tokenService.listUserTokens(SecurityContext.getUser());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody Map<String, String> body) {
    final String token = body.get("token");
    final String targetUrl = body.get("targetUrl");
    final String protectToken = body.get("protectToken");
    tokenService.create(token, targetUrl, protectToken, SecurityContext.getUser());
  }

  @PutMapping("/{token}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@PathVariable String token, @RequestBody Map<String, String> body) {
    final String targetUrl = body.get("targetUrl");
    final String protectToken = body.get("protectToken");
    tokenService.update(token, targetUrl, protectToken, SecurityContext.getUser());
  }

  @DeleteMapping("/{token}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String token) {
    tokenService.deleteToken(token, SecurityContext.getUser().getUsername());
  }

  @PutMapping("/{token}/protect")
  @ResponseStatus(HttpStatus.CREATED)
  public void protect(@PathVariable("token") String theToken, @RequestBody Map<String, String> body) {
    final Token token = tokenService.getToken(theToken, SecurityContext.getUser().getUsername());
    String protectToken = body.get("protectToken");
    tokenService.update(theToken, token.getTargetUrl(), protectToken, SecurityContext.getUser());
  }
}
```


