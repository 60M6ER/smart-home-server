package ru.bomber.trader.Specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.bomber.trader.models.Bot;

public class BotSpecifications {
    public static Specification<Bot> nameLike(String namePart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + namePart + "%");
    }
}
