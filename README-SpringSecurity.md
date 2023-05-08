# Exercises for securing the Url-shortener with Spring Security

So far we have used our own implementation of security. Now we will use Spring Security.

## Exercises
The Url-shortener have two levels of security which must be considered in the following exercises:

First there are Users who own Tokens. A User can only see and manipulate his own Tokens.

Then some Tokens are protected from being followed. A User can only follow a protected Token if he knows the protectToken. How should this be represented in Spring Security?

### Exercise 1: Add Spring Security

You must update the pom.xml to include the Spring Security dependency.

#### Solution:
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

### Exercise 2: Disable the SecurityInterceptor

We will not be using the SecurityInterceptor anymore. It is replaced by Spring Security.

Disable the configuration setting the interceptor up.

#### Solution:
Delete `dk.lundogbendsen.springbootcourse.urlshortener.controller.security.SecurityConfig`


### Exercise 3: Prepare for compliance with Spring Security

We will be using the components of Spring Security, and therefore we must convert our User to a UserDetails by implementing the UserDetails interface.

Take care to check that the implementation the UserDetails interface is returns the correct values.


#### Solution:
```java
public class User implements UserDetails {
    String username;
    String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### Exercise 4: Add a UserDetailsService

We need a UserDetailsService to load the UserDetails from the database.

We could convert the existing UserService, or we could create a new one. It is up to you.

If you create a new one, make sure to reuse the existing for actual user management.


#### Solution:
```java
@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.getUser(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(username);
        }
    }
}
```


### Exercise 5: Add a PasswordEncoder

It is required by by Spring Security to use a PasswordEncoder for encoding and verifying passwords.

Normally this is setup by setting up one of the built-in UserDetailsManager, but we are not using one of those. Therefore we must setup the PasswordEncoder ourselves.

Since we don't really encrypt password, we will use the NoOpPasswordEncoder.

Create a new SecurityConfiguration class and add a PasswordEncoder bean to it.

#### Solution:
```java
@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

### Exercise 6: Secure the /token endpoint

Now it is time to actually use Spring Security to secure the /token endpoint.

We do that by setting up a SecurityFilterChain for that section.
- It must support basic authentication
- All requests must be authenticated
- It must use our own UserDetailsService

Set all this up in the SecurityConfiguration class.



#### Solution:
```java
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MyUserDetailService userService) throws Exception {
        http
                .securityMatcher("/token/**") 
                .httpBasic()
                .and().userDetailsService(userService)
                .authorizeHttpRequests() 
                .anyRequest().authenticated(); 
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```


### Exercise 7: Passing the authenticated user to TokenService

We used the dk.lundogbendsen.springbootcourse.urlshortener.controller.security.SecurityContext to take care of the authenticated user. 

But we have disabled this feature, and now it will always return null.

We could refactor this in many ways, but here we will just make one update to the class.

Update the getUser() method to pull the authenticated user from the SecurityContextHolder.


#### Solution:
```java
public class SecurityContext {
    // ...
    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
```


### Exercise 8: Add TRACING

It is almost time for trying out the solution, but first we must have eyes.

Set loglevel for org.springframework.security to TRACE in the `application.properties` file.

#### Solution:
```properties
logging.level.org.springframework.security=TRACE
```


### Exercise 9: Try it out

Now everything should be ready to try out.

It is bad karma to just think it would work. Therefore you will find the BasicAuthenticationFilter and set a breakpoint in the doFilter() method.

Then you will use Postman to send a request to the /token endpoint.


### Exercise 10: How about adding a user on startup?

Ok - it didn't work. But why?

We are still using the in-memory database, and therefore we must add a user to the database.

One way to do this would be to hook into the Spring Bean lifecycle and use the InitializaingBean interface.

Find an appropriate class to implement this interface and add a user to the database.

#### Solution:
```java
@Service
public class UserService implements InitializingBean {
    // ...

    @Override
    public void afterPropertiesSet() throws Exception {
        create("user", "password");
        create("admin", "password");
    }
}
```


