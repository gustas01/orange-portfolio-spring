package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.repositories.ProjectsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectsService {
  private ProjectsRepository projectsRepository;
  private TagsService tagsService;
  private ModelMapper mapper;

  public ProjectsService(ProjectsRepository projectsRepository, TagsService tagsService, ModelMapper mapper) {
    this.projectsRepository = projectsRepository;
    this.tagsService = tagsService;
    this.mapper = mapper;
  }

  public Project create(CreateProjectDTO createProjectDTO){
    Project project = mapper.map(createProjectDTO, Project.class);
    List<Tag> tags = this.tagsService.findAll();

    for (String tn : createProjectDTO.getTags())
      for (Tag t : tags) {
        if (t.getTagName().equals(tn))
          project.getTags().add(t);
      }

    return this.projectsRepository.save(project);
  }

  public Page<Project> discovery(Pageable pageable){;
    Page<Project> projects = this.projectsRepository.findAll(pageable);
    return projects;
  }

  public Project findOne(UUID id) {
    Optional<Project> project = this.projectsRepository.findById(id);
    if (project.isEmpty())
      throw new EntityNotFoundException("Projeto não encontrado!");
    return project.get();
  }

  //chamar lá no userController
  public Page<ProjectDTO> findAllByAuthor(UUID id, Pageable pageable){
    return this.projectsRepository.findAllByAuthorId(id, pageable);
  }

  public ProjectDTO update(UUID id, CreateProjectDTO project) {
    return this.projectsRepository.findById(id).map(p -> {
      if(project.getTitle() != null) p.setTitle(project.getTitle());
      if(project.getDescription() != null) p.setDescription(project.getDescription());
      if(project.getUrl() != null) p.setUrl(project.getUrl());

      if((project.getTags() != null) && !project.getTags().isEmpty()){
        List<Tag> tags = this.tagsService.findAll();
        tags.forEach(p.getTags()::remove);

        for (String tn : project.getTags())
          for (Tag t : tags) {
            if (t.getTagName().equals(tn))
              p.getTags().add(t);
          }
      }
      this.projectsRepository.save(p);
      return mapper.map(p, ProjectDTO.class);
    }).orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado!"));
  }

  public String delete(UUID id) {
    if(this.projectsRepository.findById(id).isEmpty())
      throw new EntityNotFoundException("Projeto inexistente!");
    this.projectsRepository.deleteById(id);
    return "Projeto deletado com sucesso";
  }
}
