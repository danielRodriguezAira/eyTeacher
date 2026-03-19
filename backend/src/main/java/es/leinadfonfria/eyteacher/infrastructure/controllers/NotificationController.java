package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.notification.GetNotificationUseCase;
import es.leinadfonfria.eyteacher.application.services.notification.GetNotificationsByOwnerUseCase;
import es.leinadfonfria.eyteacher.application.services.notification.MarkNotificationAsReadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for notification operations.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for notification management")
public class NotificationController {
    private final GetNotificationUseCase getNotificationUseCase;
    private final GetNotificationsByOwnerUseCase getNotificationsByOwnerUseCase;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;

    /**
     * Retrieves a notification by its ID.
     *
     * @param id The ID of the notification.
     * @return ResponseEntity<?> HTTP 200 with the notification or BAD_REQUEST with an error code.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get notification", description = "Retrieves a notification by its ID.")
    public ResponseEntity<?> getNotification(@PathVariable Long id) {
        log.info("Getting notification: {}", id);
        return getNotificationUseCase.getNotification(id)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves all notifications for a given owner.
     *
     * @param ownerId The UUID of the owner.
     * @return ResponseEntity<?> HTTP 200 with the list of notifications or BAD_REQUEST with an error code.
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get notifications by owner", description = "Retrieves all notifications for the given owner.")
    public ResponseEntity<?> getNotificationsByOwner(@PathVariable UUID ownerId) {
        log.info("Getting notifications for owner: {}", ownerId);
        return getNotificationsByOwnerUseCase.getNotificationsByOwner(ownerId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Marks a notification as read.
     *
     * @param id The ID of the notification to mark as read.
     * @return ResponseEntity<?> HTTP 200 with the updated notification or BAD_REQUEST with an error code.
     */
    @GetMapping("/mark-as-read/{id}")
    @Operation(summary = "Mark notification as read", description = "Mark notification as read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        log.info("Marking notification as read: {}", id);
        return markNotificationAsReadUseCase.markNotificationAsRead(id)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}