package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SaveCategoryRequestMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "toCategoryName")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "topicList", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    Category toDomain(SaveCategoryRequest request);

    @Named("toCategoryName")
    default Name toCategoryName(String name) {
        return new Name(name);
    }
}
