# Exercises for build an Test for an API layer

Until now we have been using Postman and Curl for ad hoc trying out new features of the REST API. 
This situation is not ideal as it leaves room for Regression and it is cumbersome to test features this way,
especially in the light of the Service Layer using a Memory Database that gets wiped with every restart.

In order to have a more systematic approach we will use Unit test for testing the API.

With Unit test we can choose between 4 different strategies:
- Strategy 1: MockMVC-standalone / Mockito (no Spring)
- Strategy 2: Spring WebMvcTest (Spring slice with initialization of MockMVC) / No SpringBoot
- Strategy 3: SpringBootTest (Full SpringBoot application with MockMvc) / Fake webserver
- Strategy 4: SpringBootTest (Full SpringBoot application with TestRestTemplate) / Real webserver

The strategies are ordered from the simplest/fastest to the most complex/slowest. More complex strategies lessen the 
burden of initializing the application, but tend to be slower. Also if not modified in scope by using slices and componentscan 
they are more like integration test instead of unit test.

Choose the simplest strategy that reaches your goal of testing. For inspiration you can consult the test classes in the
package `dk/lundogbendsen/springbootcourse/urlshortener/controller` in the test folder. Here you'll find concrete working
examples of using each of the strategies.

The goal of these exercises is to cover the API in such a way that the postman collection `UrlShortener v2.postman_collection.json`
is satisfied and also cover some of the "odd-cases" that might arrive if business constraints are not met.

This is where you will have to use your imagination to create scenarioes and preconditions for each test case. Below 
is listed headlines of many normal cases and odd-cases. You job is now to implement those. If your tests are green, 
the postman collection should be able to run smoothly afterwards.


## Inspiration

The following exercises are without solutions, but there are 2 inspirational test classes that illustrate most of what 
you need.

**InspirationalUserControllerTest.java**

Show how you can use Mockity to test the Controller Layer.

**InspirationalSecurityTest.java** 

Shows how you could user @SpringBootTest to test the security part of the API.

Mind that you have a free choice of @SpringBootTest and Mockito. They have equivalent capabilities. It's a matter of
style, comfort and preference...



## User CRUD:

### Exercise: Create User

Should return 200 and the user created

### Exercise: Create User that already exists

Must setup a canned answer in the UserService that is the same as the user, you are trying to create.

Should throw an exception. And a certain status code.

### Exercise: List Users

Setup a canned answer of a couple of users. 

Should return a list of those users.

### Exercise: Get User


### Exercise: Get User that does not exist
### Exercise: Delete User
### Exercise: Delete User that does not exist


## Token:

### Exercise: Create Token
### Exercise: Create invalid Token: TokenAlreadyExists
### Exercise: Create invalid Token: IllegalTarget
### Exercise: Create invalid Token: IllegalTokenName
### Exercise: Create invalid Token: TokenTargetUriIsNull
### Exercise: Create invalid Token: InvalidTokenUrl
### Exercise: List Tokens
### Exercise: Get Token
### Exercise: Update Token
### Exercise: Delete Token
### Exercise: Protect Token
### Exercise: Unprotect Token

## Follow token
### Exercise: Follow valid token
### Exercise: Follow nonexisting token
                                                                     
## Security
### Exercise: Get Token not owned by User
### Exercise: Update Token not owned by User
### Exercise: Delete Token not owned by User
### Exercise: Follow protected token
### Exercise: Follow protected token with invalid credentials
