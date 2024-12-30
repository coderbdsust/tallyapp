package com.udayan.tallykhata.globalexception;


import com.udayan.tallykhata.auth.exp.InvalidTokenException;
import com.udayan.tallykhata.user.exp.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.udayan.tallykhata.utils.Utils.convertToTitleCase;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UsernameNotFoundException.class})
    public ResponseEntity<?> unauthorized(UsernameNotFoundException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(convertToTitleCase(HttpStatus.UNAUTHORIZED))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({
            UserAccountIsLocked.class})
    public ResponseEntity<?> unauthorized(UserAccountIsLocked ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(convertToTitleCase(HttpStatus.UNAUTHORIZED))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({
            BadCredentialsException.class})
    public ResponseEntity<?> unauthorized(BadCredentialsException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(convertToTitleCase(HttpStatus.UNAUTHORIZED))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({
            InvalidTokenException.class})
    public ResponseEntity<?> unauthorized(InvalidTokenException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(convertToTitleCase(HttpStatus.UNAUTHORIZED))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({
            UserNotActiveException.class})
    public ResponseEntity<?> unauthorized(UserNotActiveException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(convertToTitleCase(HttpStatus.UNAUTHORIZED))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({
            DuplicateKeyException.class})
    public ResponseEntity<?> badRequest(DuplicateKeyException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(convertToTitleCase(HttpStatus.BAD_REQUEST))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            InvalidDateFormat.class})
    public ResponseEntity<?> badRequest(InvalidDateFormat ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(convertToTitleCase(HttpStatus.BAD_REQUEST))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            InvalidDataException.class})
    public ResponseEntity<?> badRequest(InvalidDataException ex, HttpServletRequest request) {
        log.error("",ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(convertToTitleCase(HttpStatus.BAD_REQUEST))
                .message(ex.getMessage())
                .errors(new ArrayList<>())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
