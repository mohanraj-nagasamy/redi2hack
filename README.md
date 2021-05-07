# Realtime GraphQL using Redis streams, and Redis search 

Showcase how to use Realtime GraphQL using Redis streams and realtime search using Redis search  

**Prerequisites:**

* [Java 11](https://sdkman.io/jdks)
* [Maven 3.2+](https://sdkman.io/sdks#maven)
* [Docker](https://www.docker.com/products/docker-desktop)
* [Redis + Modules ](https://hub.docker.com/r/redislabs/redismod) 6.0.1 or greater

### Start Redis and the Spring Boot Application

1. Start the Docker Compose application:

    ```
    cd redi2hack
    docker-compose up
    ```
2. Run the Spring Boot app to build the application.

    ```
    ./mvnw clean -Dspring-boot.run.profiles=local spring-boot:run
    ```

## How it works