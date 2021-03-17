# Exercises for build an API on Url-shortener

At this point we have a fully functional Microservice with API and Persistence and Tests.

Now it is time to play with coorporation between Microservices. 

The exercises here will enable coordination of state between the Microservices of all the course partisipants UrlShorteners.

### Disclaimer 
there are several issues in the approach taken in these exercises. First, you would not coordinate state like this between instances of the same Microservice. They would rely on access to the same database. Second, if an instance restarts, it looses any state coordination sent while it was down, and therefore its state would propably get out of sync very fast.

BUT, we are here to have FUN, and Messaging is FUN. And as long as we are learning...


### Exercise 1: Add the AMQP starter
### Exercise 2: Start RabbitMQ
### Exercise 3: In the Controller Layer: Stop calling the Service Layer - send a message!
### Exercise 4: In the Service Layer: Consume messages and execute
