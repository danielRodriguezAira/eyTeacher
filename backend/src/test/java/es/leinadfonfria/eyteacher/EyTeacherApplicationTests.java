package es.leinadfonfria.eyteacher;

import es.leinadfonfria.eyteacher.config.TestRabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRabbitConfig.class)
class EyTeacherApplicationTests {

    @Test
    void contextLoads() {
    }

}
