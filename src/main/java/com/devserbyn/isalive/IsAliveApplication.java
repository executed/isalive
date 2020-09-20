package com.devserbyn.isalive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class IsAliveApplication {

    static {
        ApiContextInitializer.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(IsAliveApplication.class, args);
    }
}

