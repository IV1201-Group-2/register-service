# Register Service

## Table of Contents
* [General Info](#general-info)
* [Project Setup](#project-setup)
* [Recommended IDE Setup](#recommended-ide-setup)
* [File and Directory Semantics](#file-and-directory-semantics)
* [Environment Variables](#environment-variables)

## General info
This repository contains all files relevant to the backend Register Service:
* Service written in Spring boot application handles backend registration and validation process.
* Contains Integration tests validating all MVC architectural layers.
* Contains log configuration file (log4j.properties)

## Recommended IDE Setup
[IntelliJ IDEA](https://www.jetbrains.com/idea/)

## Project Setup

### Perform compiling, test runs and packaging
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
      - CI.yml -> On push, runs tests, static analysis, bug checks, linting and provides test coverage report.
  - src
    - main
      - java
        - com.example.registerservice
              - RegisterServiceApplication.java -> Initiates spring boot server
        - controller
          - RegisterController -> Handles HTTP requests
        - exception 
          - ExceptionHandler -> Throws a custom error in case of any general issues while the register microservice is running
        - model
          - dto
            - ErrorDTO -> Data transfer object presents various errors depending on the validation check
            - PersonDTO -> Data transfer object containing person information required for registration
          - Person -> Model representing the database structure created for each registered user
        - repository 
          - PersonRepository -> Repository that contains methods for data retrieval/modification operations.
        - security
          - SecurityConfig -> Contains configurations and security settings
          - WebConfiguration -> Configures mappings to allow Cross-origin requests.
        - service
          - PersonService -> Service handling business-logic specific to person-related operations.
   - resources
     - templates
       - application.properties -> Stores configuration properties
       - log4j.properties -> Configuration for logging
  - test
    - java
      - com.example.registerservice
        - PersonRegistrationIntegrationTest -> Integration tests covering the service layer and repository layer
        - RegistrationControllerIntegrationTest -> Integration tests covering the controller layer
- procfile -> Process types required by the app
- system.properties -> Required Heroku settings
- mwnw -> Build tool       

## Environment Variables
### Heroku config variables required by Register Service
1. DATABASE_URL
2. DATABASE_NAME
3. DATABASE_PASSWORD
