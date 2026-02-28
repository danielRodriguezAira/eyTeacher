package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.dtos.category.SaveCategoryRequest;
import es.leinadfonfria.eyteacher.application.services.category.DeleteCategoryUseCase;
import es.leinadfonfria.eyteacher.application.services.category.GetCategoriesByOwnerUseCase;
import es.leinadfonfria.eyteacher.application.services.category.GetCategoryUseCase;
import es.leinadfonfria.eyteacher.application.services.category.SaveCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for category operations.
 * Provides endpoints for category management.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for category management")
public class CategoryController {

    private final SaveCategoryUseCase saveCategoryUseCase;
    private final GetCategoriesByOwnerUseCase getCategoriesByOwnerUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieves all data of a category by its ID")
    public ResponseEntity<?> getCategory(@PathVariable Long categoryId) {
        log.info("Getting category with id: {}", categoryId);
        return getCategoryUseCase.getCategory(categoryId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get categories by owner", description = "Retrieves all categories belonging to the given owner")
    public ResponseEntity<?> getCategoriesByOwner(@PathVariable String ownerId) {
        log.info("Getting categories for owner: {}", ownerId);
        return getCategoriesByOwnerUseCase.getCategoriesByOwner(ownerId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Handles category creation or edition requests.
     * Creates or edits a new category associated to the given owner.
     *
     * @param request The category details.
     * @return ResponseEntity<?> HTTP 200 with the result: OK or BAD_REQUEST with an error code.
     */
    @PostMapping
    @Operation(summary = "Save category", description = "Creates or edits a category for the given owner")
    public ResponseEntity<?> saveCategory(@RequestBody SaveCategoryRequest request) {
        log.info("Saving category: {}", request);
        return saveCategoryUseCase.saveCategory(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Deletes a category by its ID. Only for TEACHER role.")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        return deleteCategoryUseCase.deleteCategory(categoryId)
                .fold(
                        v -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
