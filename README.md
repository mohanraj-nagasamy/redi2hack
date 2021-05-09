# Realtime GraphQL using Redis Streams, and RediSearch

Showcases how to use Realtime GraphQL using Redis Streams and real-time search using RediSearch in Java

### Deployment in Heroku
https://redi2hack.herokuapp.com/

### Start Redis and the Spring Boot Application in local dev env

**Prerequisites:**

* [Java 11](https://sdkman.io/jdks)
* [Maven 3.2+](https://sdkman.io/sdks#maven)
* [Docker](https://www.docker.com/products/docker-desktop)
* [Redis + Modules ](https://hub.docker.com/r/redislabs/redismod) 6.0.1 or greater

1. Start the Docker Compose application:

    ```
    cd redi2hack
    docker-compose up
    ```
2. Run the Spring Boot app to build the application.

    ```
    ./mvnw clean -Dspring-boot.run.profiles=local spring-boot:run
    ```
3. Goto: http://localhost:8080/playground

## Architecture
The data flows from left to right - from GraphQL->Redis and Redis->GraphQL.

## How it works
This project shows how to use **GraphQL Subscription** to push event data from the server using **Redis Streams** and support real-time search using **RediSearch**.

* RediStreams: to capture all the customer events
* RediSearch: to support search customer capabilities
* RediHash: to store customer data
* GraphQL:
   * Subscription: to push real time customer events
   * Mutation: to create/update/delete the customer state
   * Query: to query/search customer information

1. Goto: https://redi2hack.herokuapp.com/
 
Note: The app comes with the necessary sample code to follow. 
 
