package com.orange.porfolio.orange.portfolio.repositories;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProjectsRepositoryTest {
  @Autowired
  EntityManager entityManager;
  @Autowired
  ModelMapper mapper;
  @Autowired
  ProjectsRepository projectsRepository;


  @Test
  @DisplayName("should get a page of projects successfully from DB")
  void findAllByAuthorIdSuccess() {


    CreateProjectDTO projectDTO = new CreateProjectDTO("um título", "uma descrição", "http://www.umaurl.com.br");

    Project persistedProject = this.createProject(projectDTO);
    Pageable pageable = PageRequest.of(0, 10);

    Page<Project> projectDTOS = this.projectsRepository
                    .findAllByAuthorId(persistedProject.getAuthor().getId(), pageable);

    assertThat(projectDTOS).isNotNull();
    assertThat(projectDTOS.getContent().getFirst().getTitle()).isEqualTo("um título");
    assertThat(projectDTOS.getContent().getFirst().getDescription()).isEqualTo("uma descrição");
    assertThat(projectDTOS.getContent().getFirst().getUrl()).isEqualTo("http://www.umaurl.com.br");
    assertThat(projectDTOS.getContent().getFirst().getAuthor().getFirstName()).isEqualTo("gustavo");
    assertThat(projectDTOS.getContent().getFirst().getAuthor().getLastName()).isEqualTo("lima");
    assertThat(projectDTOS.getContent().getFirst().getAuthor().getEmail()).isEqualTo("gustavo@email.com");
  }

  @Test
  @DisplayName("should NOT get a page of projects successfully from DB")
  void findAllByAuthorIdFail() {

    CreateUserDTO userDTO = new CreateUserDTO(
            "gustavo", "lima", "gustavo@email.com", "12345678Aa!");
      User persistedUser = this.createUser(userDTO);

    Pageable pageable = PageRequest.of(0, 10);

    Page<Project> projectDTOS = this.projectsRepository
            .findAllByAuthorId(persistedUser.getId(), pageable);

    assertThat(projectDTOS.getContent()).isEmpty();
    assertThat(projectDTOS.getNumberOfElements()).isEqualTo(0);
  }

  private Project createProject(CreateProjectDTO data){
    CreateUserDTO userDTO = new CreateUserDTO(
            "gustavo", "lima", "gustavo@email.com", "12345678Aa!");
    User persistedUser = this.createUser(userDTO);
    Project newProject = mapper.map(data, Project.class);
    newProject.setAuthor(persistedUser);
    entityManager.persist(newProject);
    return newProject;
  }

  private User createUser(CreateUserDTO data){
    User newUser = mapper.map(data, User.class);
    newUser.getRoles().add(this.createRole("user"));
    entityManager.persist(newUser);
    return newUser;
  }

  private Role createRole(String roleName){
    Role newRole = new Role(roleName);
    entityManager.persist(newRole);
    return newRole;
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    public ModelMapper modelMapper() {
      return new ModelMapper();
    }
  }
}