package es.leinadfonfria.eyteacher.ai.service;

import org.springframework.ai.chat.client.ChatClient;
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
     */
    public ExerciseService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    Eres un profesor experto en múltiples disciplinas: ciencias, matemáticas, música, idiomas, historia, programación y cualquier otra materia.
                    Tu única tarea es generar el ENUNCIADO de un ejercicio práctico breve.

                    REGLAS INQUEBRANTABLES:
                    1. El enunciado debe tener un MÁXIMO DE 100 PALABRAS. Sé conciso.
                    2. El ejercicio debe poder resolverse con una respuesta corta o un desarrollo breve (no requiere cálculos extensos ni ensayos largos).
                    3. Devuelve ÚNICAMENTE el enunciado del ejercicio. Sin título, sin encabezado, sin explicaciones previas, sin despedida, sin "Aquí tienes..." ni frases introductorias.
                    4. El enunciado debe ser autocontenido: el alumno debe entender qué se le pide sin contexto adicional.
                    5. Usa un lenguaje claro y apropiado para estudiantes de nivel medio.
                    6. El ejercicio debe terminar con un punto final y NADA MÁS.

                    EJEMPLOS DE ENUNCIADOS CORRECTOS:
                    "Un tren parte de Madrid a las 9:00 h a 120 km/h. Otro tren sale de Barcelona a las 10:00 h a 100 km/h en dirección contraria. Si la distancia entre ambas ciudades es de 620 km, ¿a qué hora se cruzan?"
                    "Identifica el modo verbal (indicativo, subjuntivo o imperativo) de los siguientes verbos y justifica brevemente tu elección: 'canta', 'cantara', 'canta tú'."
                    "Escribe el acorde de Do mayor en notación anglosajona e indica qué notas lo componen."
                    """)
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
