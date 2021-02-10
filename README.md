# Exercises for build an API on Url-shortener

The base project includes a domain model and a service layer, and some basic testing of the service layer.

It is not even a real springboot project since it lacks a class annotated with `@SpringBootApplication`


### Exercise 1: Make the project a Springboot web project
- Include the starter-web in the pom.xml.
- create a class in the namespace root and annotate it with @SpringBootApplication.
- Also make a main method, that builds the Spring Application Context
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
  - listUsers()
  
- Make a TokenController with path prefix "/token" and the following operations:
  - createToken() (status code 201) 
  - getToken() 
  - deleteToken() (status code 204)
  - listToken()
  - updateToken()

- Make a TokenController with path prefix "/" and the following operations:
  - followToken() (status code 301)

Beware of the Security constraints of the Service Layer: Token operations can only be performed by the User owning the tokens. 
The Controller layer must therefore collect user information somehow along with the input to the Tokenoperations.


Special attention must be given to the followToken. This is where the whole business values of the service lies. The followToken operation must:
  - send a status code 301.
  - send a location header with the targetUrl of the token.
  - the Token can be protected by a "protectToken", so you should support the client providing one _if required_.

#### Solution
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/UserController.java
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/TokenController.java
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/FollowTokenController.java

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


