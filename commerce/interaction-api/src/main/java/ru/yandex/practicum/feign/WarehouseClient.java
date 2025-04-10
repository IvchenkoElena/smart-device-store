package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.WarehouseOperations;

@FeignClient(name="warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseOperations {
}
