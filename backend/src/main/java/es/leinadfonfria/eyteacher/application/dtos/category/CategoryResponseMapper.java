package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponse;
import es.leinadfonfria.eyteacher.application.dtos.auth.UserResponseMapper;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponse;
import es.leinadfonfria.eyteacher.application.dtos.topic.TopicResponseMapper;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, TopicResponseMapper.class})
public interface CategoryResponseMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "categoryNameToString")
    @Mapping(target = "ownerId", source = "owner.id", qualifiedByName = "ownerIdToString")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "name", source = "category.name", qualifiedByName = "categoryNameToString")
    @Mapping(target = "ownerId", source = "category.owner.id", qualifiedByName = "ownerIdToString")
    @Mapping(target = "topicList", source = "topicList")
    @Mapping(target = "studentList", source = "studentList")
    CategoryResponse toCategoryResponse(Category category, List<TopicResponse> topicList, List<UserResponse> studentList);

    @Named("categoryNameToString")
    default String categoryNameToString(Name name) {
        return name.value();
    }

    @Named("ownerIdToString")
    default String ownerIdToString(UserId userId) {
        return userId.value().toString();
    }

    List<CategoryResponse> toCategoryResponseList(List<Category> categoryList);
}
