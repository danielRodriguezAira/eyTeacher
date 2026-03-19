package es.leinadfonfria.eyteacher.infrastructure.persistence.repositories;

import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.NotificationJpaEntity;
import es.leinadfonfria.eyteacher.infrastructure.persistence.entities.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NotificationJpaRepositoryTest {

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private UserJpaEntity owner;

    @BeforeEach
    void setUp() {
        owner = UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .email("test" + UUID.randomUUID() + "@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .isAdmin(false)
                .build();
        entityManager.persist(owner);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debe retornar las notificaciones ordenadas: no leídas primero, luego por fecha descendente")
    void findByOwnerOrderByReadAscCreatedAtDesc_ShouldReturnCorrectOrder() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // No leída, más antigua
        NotificationJpaEntity unreadOld = NotificationJpaEntity.builder()
                .owner(owner)
                .message("Unread Old")
                .goTo("/tasks/1")
                .read(false)
                .createdAt(now.minusDays(2))
                .build();
        
        // No leída, más reciente
        NotificationJpaEntity unreadNew = NotificationJpaEntity.builder()
                .owner(owner)
                .message("Unread New")
                .goTo("/tasks/2")
                .read(false)
                .createdAt(now.minusDays(1))
                .build();
        
        // Leída, más reciente
        NotificationJpaEntity readNew = NotificationJpaEntity.builder()
                .owner(owner)
                .message("Read New")
                .goTo("/tasks/3")
                .read(true)
                .createdAt(now)
                .build();

        // Leída, más antigua
        NotificationJpaEntity readOld = NotificationJpaEntity.builder()
                .owner(owner)
                .message("Read Old")
                .goTo("/tasks/4")
                .read(true)
                .createdAt(now.minusDays(3))
                .build();

        // Persistimos en desorden para probar el ordenamiento del repo
        // Usamos SQL nativo para insertar las fechas exactas, ya que @CreationTimestamp puede ignorar los valores del builder
        insertNotification(unreadOld);
        insertNotification(readNew);
        insertNotification(unreadNew);
        insertNotification(readOld);
        
        entityManager.flush();
        entityManager.clear();

        // Act
        List<NotificationJpaEntity> result = notificationJpaRepository.findByOwnerOrderByReadAscCreatedAtDesc(owner);

        // Assert
        assertEquals(4, result.size());
        
        // El orden esperado es:
        // 1. Unread New (read=false, createdAt=now-1)
        // 2. Unread Old (read=false, createdAt=now-2)
        // 3. Read New (read=true, createdAt=now)
        // 4. Read Old (read=true, createdAt=now-3)
        
        assertEquals("Unread New", result.get(0).getMessage());
        assertEquals("Unread Old", result.get(1).getMessage());
        assertEquals("Read New", result.get(2).getMessage());
        assertEquals("Read Old", result.get(3).getMessage());
    }

    private void insertNotification(NotificationJpaEntity entity) {
        entityManager.createNativeQuery("INSERT INTO notifications (owner_id, message, go_to, is_read, created_at) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, entity.getOwner().getId())
                .setParameter(2, entity.getMessage())
                .setParameter(3, entity.getGoTo())
                .setParameter(4, entity.isRead())
                .setParameter(5, java.sql.Timestamp.valueOf(entity.getCreatedAt()))
                .executeUpdate();
    }
}
