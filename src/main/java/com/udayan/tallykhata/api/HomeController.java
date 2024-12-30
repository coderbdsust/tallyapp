package com.udayan.tallykhata.api;

import com.udayan.tallykhata.dto.Greeting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/greeting")
    public ResponseEntity<?> home() {
        Greeting greeting = new Greeting("Welcome to Tally Khata");
        return ResponseEntity.ok(greeting);
    }
}
