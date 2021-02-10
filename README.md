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

#### Solution
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/UserController.java
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/TokenController.java
See: src/main/java/dk/lundogbendsen/springbootcourse/urlshortener/controller/FollowTokenController.java

### Exercise 3: Better Exception handling


