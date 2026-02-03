package es.leinadfonfria.eyteacher.infrastructure.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping("/{name}")
    public String sayHello(@PathVariable String name) {
        return String.format("Hello %s!", name);
    }
}
