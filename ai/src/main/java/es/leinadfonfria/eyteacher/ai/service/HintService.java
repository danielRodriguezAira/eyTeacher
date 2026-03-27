package es.leinadfonfria.eyteacher.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Generates conceptual hints for student questions using an AI language model.
 *
 * <p>The service receives an exercise or question and returns a single conceptual
 * hint that guides the student towards the answer without revealing it directly.
 * This promotes autonomous learning across any academic discipline.</p>
 */
@Service
public class HintService {

    private final ChatClient chatClient;

    /**
     * Creates a {@code HintService} and configures the {@link ChatClient} with
     * the system prompt that governs hint generation behaviour.
     *
     * @param chatClientBuilder the Spring AI builder used to construct the chat client
     * @param systemPrompt      the system prompt loaded from {@code ai.prompts.hint-system}
     */
    public HintService(ChatClient.Builder chatClientBuilder,
                       @Value("${ai.prompts.hint-system}") String systemPrompt) {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    /**
     * Generates a single conceptual hint for the provided exercise or question.
     *
     * @param exercisePrompt the student's question or exercise description
     * @return the AI-generated hint
     */
    public String generateHint(String exercisePrompt) {
        return this.chatClient.prompt()
                .user(exercisePrompt)
                .call()
                .content();
    }
}
