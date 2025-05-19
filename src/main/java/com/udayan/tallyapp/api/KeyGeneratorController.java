package com.udayan.tallyapp.api;

import com.udayan.tallyapp.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key/v1")
public class KeyGeneratorController {

    @GetMapping("/otp")
    public ResponseEntity<?> otp(@RequestParam(name = "length", defaultValue = "6", required = false) Integer length) {
        return ResponseEntity.ok(Utils.generateOTP(length));
    }

    @GetMapping("/secret-key")
    public ResponseEntity<?> secretKey(@RequestParam(name = "length", defaultValue = "64", required = false) Integer length) {
        return ResponseEntity.ok(Utils.generateSecretKey(length));
    }

    @GetMapping("/salt")
    public ResponseEntity<?> salt(@RequestParam(name = "length", defaultValue = "32", required = false) Integer length) {
        return ResponseEntity.ok(Utils.generateSalt(length));
    }

    @GetMapping("/activation-key")
    public ResponseEntity<?> activationKey(@RequestParam(name = "length", defaultValue = "32", required = false) Integer length) {
        return ResponseEntity.ok(Utils.generateActivationCodeKey(length));
    }
}
