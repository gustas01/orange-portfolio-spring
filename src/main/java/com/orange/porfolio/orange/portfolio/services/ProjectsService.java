package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.exceptions.ForbiddenRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.ProjectsRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ProjectsService {
  private ProjectsRepository projectsRepository;
  private TagsService tagsService;
  private ModelMapper mapper;
  private UsersRepository usersRepository;
  private ImageUploadService imageUploadService;

  public ProjectsService(ProjectsRepository projectsRepository, TagsService tagsService,
                         ModelMapper mapper, UsersRepository usersRepository,
                         ImageUploadService imageUploadService) {
    this.projectsRepository = projectsRepository;
    this.tagsService = tagsService;
    this.mapper = mapper;
    this.usersRepository = usersRepository;
    this.imageUploadService = imageUploadService;
  }

  public ProjectDTO create(UUID userId, CreateProjectDTO createProjectDTO, MultipartFile file){
    User user = this.usersRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));
    Project project = mapper.map(createProjectDTO, Project.class);
    project.setAuthor(user);
    List<Tag> tags = this.tagsService.findAll();

    for (String tn : createProjectDTO.getTags())
      for (Tag t : tags) {
        if (t.getTagName().equals(tn))
          project.getTags().add(t);
      }
    if (project.getTags().isEmpty()) throw new BadRequestRuntimeException("Tag inexistente");

    ImgurResponse imgurResponse = this.imageUploadService.uploadImage(file);
    project.setThumbnailUrl(imgurResponse.getData().getLink());

    return mapper.map(this.projectsRepository.save(project), ProjectDTO.class);
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

  public ProjectDTO update(UUID userId, UUID id, CreateProjectDTO project) {
    return this.projectsRepository.findById(id).map(p -> {
      if (!(p.getAuthor().getId().equals(userId)))
        throw new ForbiddenRuntimeException("Você não tem autorização para atualizar projeto de outro usuário!");
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
//      if (project.getTags().isEmpty()) throw new BadRequestRuntimeException("Tag inexistente");

      this.projectsRepository.save(p);
      return mapper.map(p, ProjectDTO.class);
    }).orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado!"));
  }

  public String delete(UUID userId, UUID id) {
    return this.projectsRepository.findById(id).map(p -> {
      if (!(p.getAuthor().getId().equals(userId)))
        throw new ForbiddenRuntimeException("Você não tem autorização para deletar projeto de outro usuário!");

    this.projectsRepository.deleteById(id);
    return "Projeto deletado com sucesso";
    }).orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado!"));

  }
}
