# Register Service

## General info
This repository contains all files relevant to the backend Register Service:
* Service written in Spring boot application handles backend registration and validation process.
* Contains Integration tests validating all MVC architectural layers.
* Contains log configuration file (log4j.properties)

## Recommended IDE Setup
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

## Project Setup

### Perform compiling, test runs, and packaging
```sh
mvn clean install
```

### Run spring boot application 

```sh
mvn spring-boot:run
```

### Simulate how Heroku containerizes and launches the app with the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli)
```sh
heroku local
```

### Checkstyle linting
```sh
mvn checkstyle:check
```

### Spotbugs bug analysis

```sh
mvn spotbugs:check
```

## File and Directory Semantics
- register-service
  - .github
    - workflows
      - CI.yml -> On push, runs tests, static analysis, bug checks, linting, and provides test coverage report
  - src
    - main
      - java
        - com.example.registerservice
              - RegisterServiceApplication.java -> Initiates spring boot server
        - controller -> Handles HTTP requests
          - RegisterController
        - exception -> Throws a custom error in case of any general issues while the register microservice is running
          - ExceptionHandler 
        - model -> Model representing the database structure created for each registered user
          - dto -> Data transfer object
            - ErrorDTO 
            - PersonDTO 
          - Person 
        - repository -> Repository that contains methods for data retrieval/modification operations
          - PersonRepository 
        - security -> Contains configurations and security settings
          - SecurityConfig 
          - WebConfiguration 
        - service -> Service handling business-logic specific to person-related operations
          - PersonService 
   - resources
     - templates
       - application.properties -> Stores configuration properties
       - log4j.properties -> Configuration for logging
  - test
    - java
      - com.example.registerservice -> Integration tests covering MVC layers
        - PersonRegistrationIntegrationTest 
        - RegistrationControllerIntegrationTest
- procfile -> Process types required by the app
- system.properties -> Required Heroku settings
- mwnw -> Build tool       

## Environment Variables
### Heroku config variables required by Register Service
1. DATABASE_URL
2. DATABASE_NAME
3. DATABASE_PASSWORD
