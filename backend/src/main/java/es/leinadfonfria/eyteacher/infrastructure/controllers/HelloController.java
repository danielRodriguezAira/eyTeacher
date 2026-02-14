package es.leinadfonfria.eyteacher.infrastructure.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple test controller.
 * Provides a greeting endpoint to verify that the application is running correctly.
 */
@Slf4j
@RestController
@RequestMapping("/hello")
class HelloController {

    /**
     * Returns a personalized greeting.
     *
     * @param name The name to include in the greeting.
     * @return String A greeting message.
     */
    @GetMapping("/{name}")
    public String sayHello(@PathVariable String name) {
        log.info("Say hello to {}", name);
        return String.format("Hello %s!", name);
    }
}
