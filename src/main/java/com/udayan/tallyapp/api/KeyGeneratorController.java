package com.udayan.tallyapp.api;

import com.udayan.tallyapp.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key/v1")
public class KeyGeneratorController {

    @GetMapping("/otp")
    public ResponseEntity<?> otp() {
        return ResponseEntity.ok(Utils.generateOTP(6));
    }

    @GetMapping("/secret-key")
    public ResponseEntity<?> secretKey() {
        return ResponseEntity.ok(Utils.generateSecretKey(64));
    }

    @GetMapping("/salt")
    public ResponseEntity<?> salt() {
        return ResponseEntity.ok(Utils.generateSalt(32));
    }

    @GetMapping("/activation-key")
    public ResponseEntity<?> activationKey() {
        return ResponseEntity.ok(Utils.generateActivationCodeKey(32));
    }
}
