package com.orange.porfolio.orange.portfolio.controllers;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.services.ProjectsService;
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


  public ProjectsController(ProjectsService projectsService){
    this.projectsService = projectsService;
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
  public ResponseEntity<Project> create(@RequestBody CreateProjectDTO createProjectDTO){
    //TODO: pegar user que está logado no momento para associar à criação do project
    return new ResponseEntity<>(this.projectsService.create(createProjectDTO),HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectDTO> update(@PathVariable UUID id, @RequestBody CreateProjectDTO updateProjectDTO) {
    //TODO: pegar user que está logado no momento e verificar se é o dono do projeto pelo ID
    return ResponseEntity.ok(this.projectsService.update(id, updateProjectDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable UUID id) {
    //TODO: pegar user que está logado no momento e verificar se é o dono do projeto pelo ID
    return new ResponseEntity<>(this.projectsService.delete(id), HttpStatus.NO_CONTENT) ;
  }
}
