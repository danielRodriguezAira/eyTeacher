package es.leinadfonfria.eyteacher.infrastructure.controllers;

import es.leinadfonfria.eyteacher.application.services.task.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for task operations.
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints for task management")
public class TaskController {

    private final SaveTaskUseCase saveTaskUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final GetTasksByTopicUseCase getTasksByTopicUseCase;
    private final GetTasksByStudentIdUseCase getTasksByStudentIdUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    /**
     * Handles task creation or edition requests.
     *
     * @param request The task details.
     * @return ResponseEntity<?> HTTP 200 with the task ID or BAD_REQUEST with an error code.
     */
    @PostMapping
    @Operation(summary = "Save task", description = "Creates or edits a task for the given topic")
    public ResponseEntity<?> saveTask(@RequestBody SaveTaskRequest request) {
        log.info("Saving task: {}", request);
        return saveTaskUseCase.saveTask(request)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId The ID of the task.
     * @return ResponseEntity<?> HTTP 200 with the task detail or BAD_REQUEST with an error code.
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Get task", description = "Retrieves the detail of a task by its ID")
    public ResponseEntity<?> getTask(@PathVariable Long taskId) {
        log.info("Getting task with id: {}", taskId);
        return getTaskUseCase.getTask(taskId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves a page of tasks by topic ID.
     *
     * @param topicId The ID of the topic.
     * @param page    Zero-based page number (default 0).
     * @param size    Number of items per page (default 10).
     * @return ResponseEntity<?> HTTP 200 with the page of tasks or BAD_REQUEST with an error code.
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Get tasks by topic", description = "Retrieves a page of tasks for the given topic")
    public ResponseEntity<?> getTasksByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting tasks for topic: {}, page: {}, size: {}", topicId, page, size);
        return getTasksByTopicUseCase.getTasksByTopic(topicId, page, size)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Retrieves all tasks for a student, grouped by status, category, and topic.
     * Status order: without solution → without correction → corrected.
     *
     * @param studentId The UUID string of the student.
     * @return ResponseEntity<?> HTTP 200 with the grouped task list or BAD_REQUEST with an error code.
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get tasks by student", description = "Retrieves all tasks for a student grouped by status, category, and topic")
    public ResponseEntity<?> getTasksByStudentId(@PathVariable String studentId) {
        log.info("Getting tasks for student: {}", studentId);
        return getTasksByStudentIdUseCase.getTasksByStudentId(studentId)
                .fold(
                        ResponseEntity::ok,
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId The ID of the task to delete.
     * @return ResponseEntity<?> HTTP 200 on success or BAD_REQUEST with an error code.
     */
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Deletes a task by its ID. Only for TEACHER role.")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        log.info("Deleting task with id: {}", taskId);
        return deleteTaskUseCase.deleteTask(taskId)
                .fold(
                        v -> ResponseEntity.ok().build(),
                        error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                );
    }
}
