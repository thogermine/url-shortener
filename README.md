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

```
