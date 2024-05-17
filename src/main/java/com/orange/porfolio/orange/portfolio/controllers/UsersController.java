package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UpdateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.security.TokenService;
import com.orange.porfolio.orange.portfolio.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {
  private final UsersService usersService;
  private final TokenService tokenService;
  private final HttpServletRequest request;

  public UsersController(UsersService usersService, TokenService tokenService, HttpServletRequest request) {
    this.usersService = usersService;
    this.tokenService = tokenService;
    this.request = request;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findOne(@PathVariable UUID id) {
    return ResponseEntity.ok(this.usersService.findOne(id));
  }

  @PutMapping()
  public ResponseEntity<UserDTO> update(@RequestPart("data") @Valid UpdateUserDTO updateUserDTO,
                                        @RequestPart (value = "image", required = false) MultipartFile file) {
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return ResponseEntity.ok(this.usersService.update(userId, updateUserDTO, file));
  }

  @GetMapping("me/data")
  public ResponseEntity<UserDTO> me() {
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return ResponseEntity.ok(this.usersService.findOne(userId));
  }

}
