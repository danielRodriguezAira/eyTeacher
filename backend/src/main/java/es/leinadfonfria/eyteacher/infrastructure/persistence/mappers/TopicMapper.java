package es.leinadfonfria.eyteacher.infrastructure.persistence.mappers;

import es.leinadfonfria.eyteacher.domain.entities.Topic;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.TopicJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper for converting between Topic domain entities and JPA entities.
 * Uses MapStruct to automate the mapping of value objects and standard fields.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface TopicMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity The persistence entity.
     * @return Topic The domain entity.
     */
    @Mapping(target = "name", source = "name", qualifiedByName = "toTopicName")
    @Mapping(target = "taskList", ignore = true)
    Topic toDomain(TopicJpaEntity entity);

    @Mapping(target = "name", source = "entity.name", qualifiedByName = "toTopicName")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "category", source = "entity.category")
    @Mapping(target = "studentList", source = "studentList")
    @Mapping(target = "taskList", ignore = true)
    Topic toDomain(TopicJpaEntity entity, List<User> studentList);

    @Named("toTopicName")
    default Name toName(String name) {
        return new Name(name);
    }

    @Named("fromTopicName")
    default String fromName(Name name) {
        return name.value();
    }

    /**
     * Converts a JPA entity list to a domain entity list.
     *
     * @param entityList The persistence entity list.
     * @return List<Topic> The domain entity list.
     */
    List<Topic> toDomainList(List<TopicJpaEntity> entityList);

    /**
     * Converts a domain entity to a JPA entity.
     *
     * @param domain The domain entity.
     * @return TopicJpaEntity The persistence entity.
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "fromTopicName")
    TopicJpaEntity toEntity(Topic domain);
}
