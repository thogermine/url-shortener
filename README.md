# Exercises for testing the API on Url-shortener

At this point we have a working Rest-api. There is a Postman collection with which we can test the api when started in a webserver.

The task is now to make inside server tests using MockMvc. The test should cover the same operations as the Postman collection plus additional to test odd-case behaviour.



## Exercise 1: Prepare test environment for Testing UserController
Test creation of a User. There are a few cases here. One where the User does not exists, and one where it already exists and an exception is thrown.
Since we are not dependant on security, we can use Strategy 1 to create a Test-environment.

* Create a Test class for testing UserController
* Create a MockMvc standalone with UserController as test target.

