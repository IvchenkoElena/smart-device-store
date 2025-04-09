package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="warehouse.products")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;
    private Boolean fragile;
    @NotNull
    private Double width;
    @NotNull
    private Double height;
    @NotNull
    private Double depth;
    @NotNull
    private Double weight;
    private Integer quantity = 0;
}
