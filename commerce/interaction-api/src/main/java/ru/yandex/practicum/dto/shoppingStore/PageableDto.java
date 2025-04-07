package ru.yandex.practicum.dto.shoppingStore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PageableDto {
    @NotNull
    @Min(0)
    Integer page;
    @NotNull
    @Min(1)
    Integer size;
    List<String> sort;
}
