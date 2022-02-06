# Use Case: URL Shortener

At this point, we have a Springboot Application with a Service Layer and a Domain Model.

Now it is time for testing the service layer.


### Exercise 1: Add the file structure for testing
- In the src folder make a test/java folder (two folders).

### Exercise 2: Preparing the Test of the UserService
The UserService have a dependency on TokenService. Therefore we must find a way to mock that dependency.

- Goto UserService and generate a test for the service.
Hint: Bring up the Generate menu with Ctrl-N and select test...
  
- Use Mockito by annotating the generated test class with `@ExtendWith(MockitoExtension.class)`
- Make a Mock of the TokenService.
- Make a Spy of the UserService users. We are using the actual functionality of the HashMap storage, but we want to see if it gets called.
- Make a UserService and inject the Mock.
Hint: Use @InjectMocks annotation.
  
#### Solution
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    TokenService tokenService;
    @Spy
    HashMap<String, User> users;
    @InjectMocks
    UserService userService;
}
```

### Exercise 3: Add a unit test for creating a User

- Create a method `public void createUserTest()` and annotate it with @Test.
- Use the UserService instance to create a User.
- Verify that the users put() was called 1 time.

#### Solution
```java
    @Test
    public void createUserTest() {
        final User user = userService.create("user1", "password1");
        verify(users, times(1)).put("user1", user);
    }
```


### Exercise 4: Add a unit test for getting a user

- Create a method `public void getUserTest()` and annotate it with @Test.
- In the method setup a when-then statement on users that returns a User with a username and password when called with a username. 
- Use the UserService instance to get a User.
- Assert that the user have the expected username and password.

#### Solution
```java
    @Test
    public void getUserTest() {
        when(users.get("user1")).thenReturn(User.builder().username("fakeuser").password("password1").build());
        final User fakeUser = userService.getUser("user1");
        assertEquals("password1", fakeUser.getPassword());
    }
```

### Exercise 5: Create unit tests for TokenService
Now it is time to test the TokenService. In this exercise you are asked to construct a unit test per method in the TokenService.
And for each method test each of the possible ways through the method including normal cases and exceptions.

This could potentially be many many unit test, depending on the cyclomatic complexity of your code. Therefore, just select a handful or so to actually implement.

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks
    TokenService tokenService;
    private User user = User.builder().username("username").password("password").build();

    @Test
    @DisplayName("create token with the name 'token' (fails)")
    public void testCreateTokenWithTheNameToken() {
        try {
            tokenService.create("token", "https://dr.dk", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token that already exists (fails)")
    public void testCreateTokenThatAlreadExists() {
        try {
            tokenService.create("token1", "https://dr.dk", null, user);
            tokenService.create("token1", "https://dr.dk", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token without a targetUrl (fails)")
    public void testCreateTokenWithoutTargetUrl() {
        try {
            tokenService.create("token1", null, null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with an invalid targetUrl (fails)")
    public void testCreateTokenWithInvalidTargetUrl() {
        try {
            tokenService.create("token1", "htt", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with a targetUrl containing localhost (fails)")
    public void testCreateTokenWithTargetUrlContainingLocalhost() {
        try {
            tokenService.create("token1", "http://localhost:8080/abc", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with a legal targetUrl (success)")
    public void testCreateTokenWithLocalTargetUrl() {
        tokenService.create("abc", "https://dr.dk", "pt1", user);
    }
}
```
