package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
public class App
{
    @RequestMapping("/")
    public String home() {
            return "Hello World!";
    }
    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }
}
