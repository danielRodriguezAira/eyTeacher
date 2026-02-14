package es.leinadfonfria.eyteacher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the EyTeacher Spring Boot application.
 * Bootstraps the application and starts the embedded server.
 */
@SpringBootApplication
public class EyTeacherApplication {

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(EyTeacherApplication.class, args);
    }

}
