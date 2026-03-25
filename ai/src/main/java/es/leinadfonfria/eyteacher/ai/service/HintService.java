package es.leinadfonfria.eyteacher.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class HintService {

    private final ChatClient chatClient;

    public HintService(ChatClient.Builder chatClientBuilder) {
        System.out.println("[DEBUG_LOG] HintService initialized with ChatClient.Builder");
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    Eres un tutor experto. Tu objetivo es dar UNA ÚNICA PISTA conceptual para que el usuario resuelva su duda por sí mismo (ya sea de gramática, programación, matemáticas, etc.).
                    
                    REGLAS INQUEBRANTABLES:
                    1. NUNCA des la respuesta final ni la solución directa.
                    2. Da solo la pista o el concepto clave necesario.
                    3. NUNCA hagas preguntas de seguimiento.
                    4. NUNCA ofrezcas más ayuda al final del mensaje.
                    5. Tu respuesta debe terminar con un punto final y NADA MÁS.
                    
                    EJEMPLO DE INTERACCIÓN:
                    Usuario: "¿Cuál es el sujeto en la frase 'El perro corre rápido'?"
                    Tú: "Para identificar el sujeto de una oración, debes preguntarle '¿quién?' o '¿quiénes?' al verbo principal de la frase."
                    """)
                .build();
    }

    public String generateHint(String exercisePrompt) {
        return this.chatClient.prompt()
                .user(exercisePrompt)
                .call()
                .content();
    }
}
