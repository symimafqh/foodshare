package com.heroku.java; // Ensure this matches your package structure

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FoodshareApplication { // You can name this class as needed

    public static void main(String[] args) {
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

        SpringApplication.run(FoodshareApplication.class, args); // This line starts your application
    }
}
