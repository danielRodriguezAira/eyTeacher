package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.dtos.topic.SaveTopicRequest;
import es.leinadfonfria.eyteacher.application.services.topic.DeleteTopicUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.GetTopicUseCase;
import es.leinadfonfria.eyteacher.application.services.topic.SaveTopicUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for topic operations.
 * Provides endpoints for topic management.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Endpoints for topic management")
public class TopicController {

    private final SaveTopicUseCase saveTopicUseCase;
    private final GetTopicUseCase getTopicUseCase;
    private final DeleteTopicUseCase deleteTopicUseCase;

    @GetMapping("/{topicId}")
    @Operation(summary = "Get topic by ID", description = "Retrieves all data of a topic by its ID")
    public ResponseEntity<?> getTopic(@PathVariable Long topicId) {
        log.info("Getting topic with id: {}", topicId);
        return getTopicUseCase.getTopic(topicId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Creates or edits a topic.
     * @param request The topic details.
     * @return ResponseEntity<?> HTTP 200 with the result: OK or BAD_REQUEST with an error code.
     */
    @PostMapping
    @Operation(summary = "Save topic", description = "Creates or edits a topic")
    public ResponseEntity<?> saveTopic(@RequestBody SaveTopicRequest request) {
        log.info("Saving topic: {}", request);
        return saveTopicUseCase.saveTopic(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    @DeleteMapping("/{topicId}")
    @Operation(summary = "Delete topic", description = "Deletes a topic by its ID. Only for TEACHER role.")
    public ResponseEntity<?> deleteTopic(@PathVariable Long topicId) {
        log.info("Deleting topic with id: {}", topicId);
        return deleteTopicUseCase.deleteTopic(topicId)
                .fold(
                        v -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
