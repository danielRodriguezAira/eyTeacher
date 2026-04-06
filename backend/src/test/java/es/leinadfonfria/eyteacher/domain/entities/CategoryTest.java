package es.leinadfonfria.eyteacher.domain.entities;

import es.leinadfonfria.eyteacher.domain.valueobjects.Email;
import es.leinadfonfria.eyteacher.domain.valueobjects.Name;
import es.leinadfonfria.eyteacher.domain.valueobjects.Password;
import es.leinadfonfria.eyteacher.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.create(
                UserId.generate(),
                new Email("owner@example.com"),
                Password.hashed("hashedPassword"),
                new Name("John"),
                new Name("Doe"),
                false
        );
    }

    @Test
    @DisplayName("Should create Category using factory method")
    void shouldCreateCategoryWithNullId() {
        Category category = Category.create( new Name("Science"), "Science category", owner);

        assertNotNull(category);
        assertNull(category.getId());
        assertEquals("Science", category.getName().value());
        assertEquals("Science category", category.getDescription());
        assertEquals(owner, category.getOwner());
    }

    @Test
    @DisplayName("Should create Category with null description")
    void shouldCreateCategoryWithNullDescription() {
        Category category = Category.create(new Name("History"), null, owner);

        assertNotNull(category);
        assertEquals("History", category.getName().value());
        assertNull(category.getDescription());
        assertEquals(owner, category.getOwner());
    }

    @Test
    @DisplayName("Should edit a Category with id")
    void shouldEditCategoryWithId() {
        Category category = Category.edit(1L, new Name("Math"), "Mathematics category");

        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("Math", category.getName().value());
        assertEquals("Mathematics category", category.getDescription());
        assertNull(category.getOwner());
    }

    @Test
    @DisplayName("Should create Category using builder")
    void shouldCreateCategoryUsingBuilder() {
        Category category = Category.builder()
                .id(2L)
                .name(new Name("Art"))
                .description("Art category")
                .owner(owner)
                .build();

        assertNotNull(category);
        assertEquals(2L, category.getId());
        assertEquals("Art", category.getName().value());
        assertEquals("Art category", category.getDescription());
        assertEquals(owner, category.getOwner());
    }
}
