package com.example.registerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RegisterServiceApplication is the class used to initialize and run the
 * register-service microservice for the project IV1201VT24.
 */
@SpringBootApplication
public class RegisterServiceApplication {

    /**
     * Main method used to start the register-service microservice.
     * @param args is the command-line argument.
     */
    public static void main(String[] args) {
        SpringApplication.run(RegisterServiceApplication.class, args);
    }

}

