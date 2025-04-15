package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.shoppindCart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CartMapper {

    ShoppingCart toCart(ShoppingCartDto cartDto);

    ShoppingCartDto toCartDto(ShoppingCart cart);
}
