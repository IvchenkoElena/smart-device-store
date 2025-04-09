package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.WarehouseProduct;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, UUID> {
    List<WarehouseProduct> findAllByProductIdIn(Set<UUID> uuids);
}
