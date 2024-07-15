package com.orange.porfolio.orange.portfolio.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;
  @Value("${api.security.token.expiration-hours}")
  private String jwtExpirationHours = "2";

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid LoginUserDTO loginUserDTO, HttpServletResponse httpServletResponse) throws JsonProcessingException {
    Cookie cookie = new Cookie("token", authService.login(loginUserDTO));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setMaxAge(60 * 60 * Integer.parseInt(jwtExpirationHours));
    cookie.setAttribute("SameSite", "None");

    httpServletResponse.addCookie(cookie);
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> res = new HashMap<>();
    res.put("message", "Usuário logado com sucesso!");
    String json = mapper.writeValueAsString(res);

    return ResponseEntity.ok(json);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestPart("data") @Valid CreateUserDTO userDTO,
                                          @RequestPart (value = "image", required = false) MultipartFile file) throws BadRequestException {
    UserDTO user = authService.register(userDTO, file);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @GetMapping("/login/google")
  public ResponseEntity<String> loginGoole(Authentication authentication, HttpServletResponse httpServletResponse) throws JsonProcessingException {
    OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

    Cookie cookie = new Cookie("token", authService.loginWithGoogle(oAuth2AuthenticationToken));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setAttribute("SameSite", "None");

    httpServletResponse.addCookie(cookie);
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> res = new HashMap<>();
    res.put("message", "Usuário logado com sucesso!");
    String json = mapper.writeValueAsString(res);

    return ResponseEntity.ok(json);
  }


  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletResponse httpServletResponse) throws JsonProcessingException {
    Cookie cookie = new Cookie("token", "");
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setMaxAge(0);
    cookie.setAttribute("SameSite", "None");

    httpServletResponse.addCookie(cookie);
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> res = new HashMap<>();
    res.put("message", "Logout realizado com sucesso!");
    String json = mapper.writeValueAsString(res);

    return ResponseEntity.ok(json);
  }
}
