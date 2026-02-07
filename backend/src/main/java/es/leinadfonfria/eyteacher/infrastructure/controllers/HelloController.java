package es.leinadfonfria.eyteacher.infrastructure.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping("/{name}")
    public String sayHello(@PathVariable String name) {
        log.info("Say hello to {}", name);
        return String.format("Hello %s!", name);
    }
}
