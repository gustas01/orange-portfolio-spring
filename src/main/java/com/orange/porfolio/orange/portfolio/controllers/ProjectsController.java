package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.security.TokenService;
import com.orange.porfolio.orange.portfolio.services.ProjectsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectsController {
  private ProjectsService projectsService;
  private TokenService tokenService;
  private HttpServletRequest request;


  public ProjectsController(ProjectsService projectsService, TokenService tokenService, HttpServletRequest request){
    this.projectsService = projectsService;
    this.tokenService = tokenService;
    this.request = request;
  }

  @GetMapping("/discovery")
  public ResponseEntity<Page<Project>> discovery(
          @RequestParam(value = "page", defaultValue = "0") Integer page,
          @RequestParam(value = "size", defaultValue = "10") Integer size){

    Pageable pageable = PageRequest.of(page, size);
    Page<Project> projects = this.projectsService.discovery(pageable);

    return ResponseEntity.ok(projects) ;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Project> findOne(@PathVariable UUID id) {
    return ResponseEntity.ok(this.projectsService.findOne(id));
  }

  @PostMapping
  public ResponseEntity<Project> create(@RequestBody @Valid CreateProjectDTO createProjectDTO, HttpServletRequest request){
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return new ResponseEntity<>(this.projectsService.create(userId, createProjectDTO),HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectDTO> update(@PathVariable UUID id, @RequestBody @Valid CreateProjectDTO updateProjectDTO) {
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return ResponseEntity.ok(this.projectsService.update(userId, id, updateProjectDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable UUID id) {
    String token = this.tokenService.recoverToken(request);
    UUID userId = UUID.fromString(this.tokenService.validateToken(token));
    return new ResponseEntity<>(this.projectsService.delete(userId, id), HttpStatus.NO_CONTENT) ;
  }
}
