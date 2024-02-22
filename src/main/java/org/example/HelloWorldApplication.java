package org.example;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** This class serves as an entry point for the Spring Boot app. */
@SpringBootApplication
public class HelloWorldApplication {

  



  public static void main( String[] args) {
    try{
      org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HelloWorldApplication.class);
      String port = System.getenv("PORT");
      if (port == null) {
        port = "8080";
        logger.warn("$PORT environment variable not set, defaulting to 8080");
      }
      SpringApplication app = new SpringApplication(HelloWorldApplication.class);
      app.setDefaultProperties(Collections.singletonMap("server.port", port));
  
      // Start the Spring Boot application.
      app.run(args);
      logger.info(
          "Hello from Cloud Run! The container started successfully and is listening for HTTP requests on " + port);
    }
    catch(Exception e) {
      System.out.println(e.getMessage());
      SpringApplication.run(HelloWorldApplication.class, args);
    }
    finally {
      SpringApplication.run(HelloWorldApplication.class, args);
    }
    // Start the Spring Boot application.

}
}
