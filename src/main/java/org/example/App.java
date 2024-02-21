package org.example;


import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** This class serves as an entry point for the Spring Boot app. */
@SpringBootApplication
public class App {



  public static void main(final String[] args) throws Exception {
    String port = System.getenv("PORT");
   
    SpringApplication app = new SpringApplication(App.class);
    System.out.println("Application started on PORT" + port);
    // Start the Spring Boot application.
    app.run(args);
}
}
