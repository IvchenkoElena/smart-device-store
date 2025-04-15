package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

    Product toProduct(ProductDto productDto);

    ProductDto toProductDto(Product product);
}