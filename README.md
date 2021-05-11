# Realtime GraphQL using Redis Streams, and RediSearch

Showcases how to use Realtime GraphQL using Redis Streams and real-time search using RediSearch in Java

### Previews

##### GraphQL playground tab3

![image](https://user-images.githubusercontent.com/2755263/117599951-298ea100-b108-11eb-9968-1d1e3a6957c3.png)

##### GraphQL playground tab1

![image](https://user-images.githubusercontent.com/2755263/117599354-e5e76780-b106-11eb-8d41-dac8dd54541d.png)


### Deployment in Heroku

[redi2hack.herokuapp.com](https://redi2hack.herokuapp.com/)

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

### Highlevel diagram

GraphQL is supported using Spring Boot which is backed by Redis enterprise data store.

![archi-1](https://user-images.githubusercontent.com/2755263/117556723-2cff2b00-b029-11eb-8312-e405c5a17692.png)

### Dataflow diagram
The data flows from left to right - from GraphQL->Redis and Redis->GraphQL.

![archi-2](https://user-images.githubusercontent.com/2755263/117556725-325c7580-b029-11eb-8319-e27ef7e5cb74.png)

* `createCustomer`, `updateCustomer`, and `deleteCustomer` GraphQL mutations use Redis hash and stream data to store the
state and push the events.

* `subscribeEvents` (GraphQL subscription) uses Redis Stream to fetch all customer events or the
latest customer events.
  
* `searchCustomers` GraphQL query uses RediSearch to search customers. 

* `customers` and `findCustomer` use Redis Hash to find the all or a particular customer.

## How it works

This project shows how to use **GraphQL Subscription** to push event data from the server using **Redis Streams** and
support real-time search using **RediSearch**.

* RediStreams: to capture all the customer events
* RediSearch: to support search customer capabilities
* RediHash: to store customer data
* GraphQL:
    * Subscription: to push real time customer events
    * Mutation: to create/update/delete the customer state
    * Query: to query/search customer information

1. Goto: [redi2hack.herokuapp.com](https://redi2hack.herokuapp.com/)

Note: The app comes with the necessary sample code to follow. 

## Deployment

To make deploys work, you need to create free account in https://redislabs.com/try-free/

### Google Cloud Run

[![Run on Google
Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run/?git_repo=https://deploy.cloud.run/?git_repo=https://github.com/mohanraj-nagasamy/redi2hack.git&revision=feature/deploy-buttons)

### Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/mohanraj-nagasamy/redi2hack)

### Vercel

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/git/external?repository-url=https://github.com/mohanraj-nagasamy/redi2hack&env=REDIS_ENDPOINT_URI,REDIS_PASSWORD)