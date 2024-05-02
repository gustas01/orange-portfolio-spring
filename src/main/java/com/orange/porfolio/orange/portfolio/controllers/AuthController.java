package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginUserDTO loginUserDTO, HttpServletResponse response) throws BadRequestException {
    Cookie cookie = new Cookie("token", authService.login(loginUserDTO));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    response.addCookie(cookie);
    return ResponseEntity.ok("Usuário logado com sucesso!");
  }

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody CreateUserDTO userDTO) throws BadRequestException {
    User user = authService.register(userDTO);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }
}
