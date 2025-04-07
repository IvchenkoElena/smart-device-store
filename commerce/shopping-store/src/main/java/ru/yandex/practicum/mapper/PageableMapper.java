package ru.yandex.practicum.mapper;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.dto.shoppingStore.PageableDto;

public class PageableMapper {
    public static Pageable toPageable(PageableDto pageableDto) {
        if (pageableDto.getSort() == null || pageableDto.getSort().isEmpty()) {
            return PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        }
        Sort sort = Sort.by(
                pageableDto.getSort().stream()
                        .map(sortStr -> {
                            String[] sortParams = sortStr.split(",");
                            if (sortParams.length == 2 && sortParams[1].equalsIgnoreCase("desc")) {
                                return Sort.Order.desc(sortParams[0]);
                            }
                            return Sort.Order.asc(sortParams[0]);
                        })
                        .toList()
        );
        return PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), sort);
    }
}
