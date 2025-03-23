package ru.yandex.practicum;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HubRouterApp {
    public static void main(String[] args) {
        SpringApplication.run(HubRouterApp.class, args);
    }
}