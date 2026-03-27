package es.leinadfonfria.eyteacher.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Generates short exercise proposals using an AI language model.
 *
 * <p>The service receives a task description (topic, subtopic and intent) and returns
 * a self-contained exercise statement suitable for a micro-learning context.
 * Exercises are constrained to a maximum of 100 words and must not require
 * overly long or complex solutions.</p>
 */
@Service
public class ExerciseService {

    private final ChatClient chatClient;

    /**
     * Creates an {@code ExerciseService} and configures the {@link ChatClient} with
     * the system prompt that governs exercise generation behaviour.
     *
     * @param chatClientBuilder the Spring AI builder used to construct the chat client
     * @param systemPrompt      the system prompt loaded from {@code ai.prompts.exercise-system}
     */
    public ExerciseService(ChatClient.Builder chatClientBuilder,
                           @Value("${ai.prompts.exercise-system}") String systemPrompt) {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    /**
     * Generates an exercise statement based on the provided task description.
     *
     * @param task a description of the topic and type of exercise requested,
     *             e.g. "Física - Mecánica - Genérame un ejercicio sobre las leyes de Newton"
     * @return the AI-generated exercise statement
     */
    public String generateExercise(String task) {
        return this.chatClient.prompt()
                .user(task)
                .call()
                .content();
    }
}
