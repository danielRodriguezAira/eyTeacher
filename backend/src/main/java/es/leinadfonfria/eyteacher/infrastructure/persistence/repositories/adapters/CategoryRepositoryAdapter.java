package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.adapters;

import es.leinadfonfria.eyteacher.domain.entities.Category;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.CategoryRepository;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.CategoryJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.mappers.CategoryMapper;
import es.leinadfonfria.eyteacher.infrastructure.persistence.repositories.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository<Category> {
    
    private final CategoryJpaRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    public List<Category> findByOwnerId(UserId ownerId) {
        return categoryRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId.value())
                .map(categoryMapper::toDomainList)
                .orElseThrow(() -> new NotFoundException("Category owner not found", ErrorCode.CATEGORY_USER_NOT_FOUND));
    }

    @Override
    public List<Category> findByStudentId(UserId studentId) {
        return categoryRepository.findByStudentIdOrderByCreatedAtDesc(studentId.value())
                .map(categoryMapper::toDomainList)
                .orElseThrow(() -> new NotFoundException("Category student not found", ErrorCode.CATEGORY_USER_NOT_FOUND));
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDomain)
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public Category save(Category category) {
        return categoryMapper.toDomain(
                categoryRepository.save(
                        categoryMapper.toEntity(category)));
    }

    @Override
    public Category update(Category category) {
        CategoryJpaEntity existing = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_NOT_FOUND));
        existing.setName(category.getName().value());
        existing.setDescription(category.getDescription());
        return categoryMapper.toDomain(categoryRepository.save(existing));
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
