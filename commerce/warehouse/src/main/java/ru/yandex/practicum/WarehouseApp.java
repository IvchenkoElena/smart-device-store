package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.api.ShoppingStoreOperations;

@EnableFeignClients(clients = {ShoppingStoreOperations.class})
@SpringBootApplication
public class WarehouseApp {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApp.class, args);
    }
}
