package com.orange.porfolio.orange.portfolio.filters;

import com.orange.porfolio.orange.portfolio.DTOs.StandardError;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.exceptions.ForbiddenRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class ExceptionsHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<StandardError> resourceNotFound(EntityNotFoundException e, HttpServletRequest request){
    String error = "Resource not found";
    HttpStatus status = HttpStatus.NOT_FOUND;
    StandardError err = new StandardError(LocalDateTime.now(), status.value(), error, e.getMessage());
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(BadRequestRuntimeException.class)
  public ResponseEntity<StandardError> badRequest(BadRequestRuntimeException e, HttpServletRequest request){
    String error = "Bad request";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    StandardError err = new StandardError(LocalDateTime.now(), status.value(), error, e.getMessage());
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(ForbiddenRuntimeException.class)
  public ResponseEntity<StandardError> forbidden(ForbiddenRuntimeException e, HttpServletRequest request){
    String error = "Forbidden";
    HttpStatus status = HttpStatus.FORBIDDEN;
    StandardError err = new StandardError(LocalDateTime.now(), status.value(), error, e.getMessage());
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardError> validations(MethodArgumentNotValidException e, HttpServletRequest request){
    String error = "Bad request";
    HttpStatus status = HttpStatus.BAD_REQUEST;

    List<String> errors = new LinkedList<>();
    String[] errorsMsg = Objects.requireNonNull(e.getDetailMessageArguments())[1].toString().split(", and ");

    for (var a : e.getDetailMessageArguments())
      System.out.println(a);
    for (String s : errorsMsg)
      errors.add(s.split(": ")[1]);

    StandardError err = new StandardError(LocalDateTime.now(), status.value(), error, errors);
    return ResponseEntity.status(status).body(err);
  }
}
