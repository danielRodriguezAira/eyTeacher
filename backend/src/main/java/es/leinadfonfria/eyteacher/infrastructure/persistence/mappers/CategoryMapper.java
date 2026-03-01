package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting between Category domain entities and JPA entities.
 * Uses MapStruct to automate the mapping of value objects and standard fields.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CategoryMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity The persistence entity.
     * @return Category The domain entity.
     */
    @Mapping(target = "name", source = "name", qualifiedByName = "toCategoryName")
    @Mapping(target = "topicList", ignore = true)
    Category toDomain(CategoryJpaEntity entity);

    @Named("toCategoryName")
    default Name toName(String name) {
        return new Name(name);
    }

    @Named("fromCategoryName")
    default String fromName(Name name) {
        return name.value();
    }

    /**
     * Converts a domain entity to a JPA entity.
     *
     * @param domain The domain entity.
     * @return CategoryJpaEntity The persistence entity.
     */
    @Mapping(target = "name", source = "name", qualifiedByName = "fromCategoryName")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "topicList", ignore = true)
    CategoryJpaEntity toEntity(Category domain);
}
