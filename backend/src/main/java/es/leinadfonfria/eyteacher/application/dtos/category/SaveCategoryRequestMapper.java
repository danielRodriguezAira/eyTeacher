package es.leinadfonfria.eyteacher.application.dtos.category;

import es.leinadfonfria.eyteacher.application.shared.ValueObjectMapper;
import es.leinadfonfria.eyteacher.domain.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ValueObjectMapper.class})
public interface SaveCategoryRequestMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "toName")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "topicList", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    Category toDomain(SaveCategoryRequest request);
}
