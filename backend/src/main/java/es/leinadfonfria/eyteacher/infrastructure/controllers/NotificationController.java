package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.notification.GetNotificationUseCase;
import es.leinadfonfria.eyteacher.application.services.notification.GetNotificationsByOwnerUseCase;
import es.leinadfonfria.eyteacher.application.services.notification.MarkNotificationAsReadUseCase;
import es.leinadfonfria.eyteacher.domain.entities.Role;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
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
     * Retrieves a page of notifications for a given owner, filtered by the notification
     * types allowed for the authenticated user's role.
     *
     * @param ownerId The UUID of the owner.
     * @param page    Zero-based page number (default 0).
     * @param size    Number of items per page (default 10).
     * @return ResponseEntity<?> HTTP 200 with the page of notifications or BAD_REQUEST with an error code.
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get notifications by owner", description = "Retrieves a page of notifications for the given owner, filtered by the caller's role.")
    public ResponseEntity<?> getNotificationsByOwner(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Role role = AuthenticationUtils.isTeacher() ? Role.TEACHER : Role.STUDENT;
        log.info("Getting notifications for owner: {}, role: {}, page: {}, size: {}", ownerId, role, page, size);
        return getNotificationsByOwnerUseCase.getNotificationsByOwner(ownerId, role, page, size)
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