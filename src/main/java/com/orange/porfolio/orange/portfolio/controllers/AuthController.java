package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid LoginUserDTO loginUserDTO, HttpServletResponse httpServletResponse) {
    Cookie cookie = new Cookie("token", authService.login(loginUserDTO));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    httpServletResponse.addCookie(cookie);
    return ResponseEntity.ok("Usuário logado com sucesso!");
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestPart("data") @Valid CreateUserDTO userDTO,
                                          @RequestPart (value = "image", required = false) MultipartFile file) throws BadRequestException {
    UserDTO user = authService.register(userDTO, file);
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
