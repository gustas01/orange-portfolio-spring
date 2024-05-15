package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid LoginUserDTO loginUserDTO, HttpServletResponse response) throws BadRequestException {
    Cookie cookie = new Cookie("token", authService.login(loginUserDTO));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    response.addCookie(cookie);
    return ResponseEntity.ok("Usuário logado com sucesso!");
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody @Valid CreateUserDTO userDTO) throws BadRequestException {
    UserDTO user = authService.register(userDTO);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @GetMapping("/login/google")
  public ResponseEntity<String> loginGoole(Authentication authentication, HttpServletResponse response){
    OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
    Cookie cookie = new Cookie("token", authService.loginWithGoogle(oAuth2AuthenticationToken));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    response.addCookie(cookie);
    return ResponseEntity.ok("Usuário logado com sucesso!");
  }
}
