package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.security.TokenService;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import com.orange.porfolio.orange.portfolio.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {
  private UsersService usersService;
  private AuthService authService;
  private TokenService tokenService;

  public UsersController(UsersService usersService, AuthService authService, TokenService tokenService) {
    this.usersService = usersService;
    this.authService = authService;
    this.tokenService = tokenService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findOne(@PathVariable UUID id) {
    return ResponseEntity.ok(this.usersService.findOne(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> update(@PathVariable UUID id, @RequestBody CreateUserDTO updateUserDTO) {
    return ResponseEntity.ok(this.usersService.update(id, updateUserDTO));
  }

  @GetMapping("me/data")
  public ResponseEntity<UserDTO> me(HttpServletRequest request) {
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return ResponseEntity.ok(this.usersService.findOne(userId));
  }

}
