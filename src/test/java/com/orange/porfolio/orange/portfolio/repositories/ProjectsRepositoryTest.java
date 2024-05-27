package com.orange.porfolio.orange.portfolio.repositories;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//import static org.assertj.core.api.Assertions.*;

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
    TestUtilsMocks mocksObjects = new TestUtilsMocks();
    User mockUser = mocksObjects.mockUser;
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;


    Project persistedProject = this.createProject(mockCreateProjectDTO);
    Pageable pageable = PageRequest.of(0, 10);

    Page<Project> projectDTOS = this.projectsRepository
                    .findAllByAuthorId(persistedProject.getAuthor().getId(), pageable);

    assertNotNull(projectDTOS);
    assertEquals(projectDTOS.getContent().getFirst().getTitle(), mockCreateProjectDTO.getTitle());
    assertEquals(projectDTOS.getContent().getFirst().getDescription(), mockCreateProjectDTO.getDescription());
    assertEquals(projectDTOS.getContent().getFirst().getUrl(), mockCreateProjectDTO.getUrl());
    assertEquals(projectDTOS.getContent().getFirst().getAuthor().getFirstName(), mockUser.getFirstName());
    assertEquals(projectDTOS.getContent().getFirst().getAuthor().getLastName(), mockUser.getLastName());
    assertEquals(projectDTOS.getContent().getFirst().getAuthor().getEmail(), mockUser.getEmail());

    //usando assertJ
//    assertThat(projectDTOS).isNotNull();
//    assertThat(projectDTOS.getContent().getFirst().getTitle()).isEqualTo(mockCreateProjectDTO.getTitle());
//    assertThat(projectDTOS.getContent().getFirst().getDescription()).isEqualTo(mockCreateProjectDTO.getDescription());
//    assertThat(projectDTOS.getContent().getFirst().getUrl()).isEqualTo(mockCreateProjectDTO.getUrl());
//    assertThat(projectDTOS.getContent().getFirst().getAuthor().getFirstName()).isEqualTo(mockUser.getFirstName());
//    assertThat(projectDTOS.getContent().getFirst().getAuthor().getLastName()).isEqualTo(mockUser.getLastName());
//    assertThat(projectDTOS.getContent().getFirst().getAuthor().getEmail()).isEqualTo(mockUser.getEmail());
  }

  @Test
  @DisplayName("should NOT get a page of projects successfully from DB")
  void findAllByAuthorIdFail() {
    TestUtilsMocks mocksObjects = new TestUtilsMocks();
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;

    User persistedUser = this.createUser(mockCreateUserDTO);

    Pageable pageable = PageRequest.of(0, 10);

    Page<Project> projectDTOS = this.projectsRepository
            .findAllByAuthorId(persistedUser.getId(), pageable);

    assertEquals(projectDTOS.getContent(), List.of());
    assertEquals(projectDTOS.getNumberOfElements(), 0);

    //usando assertJ
//    assertThat(projectDTOS.getContent()).isEmpty();
//    assertThat(projectDTOS.getNumberOfElements()).isEqualTo(0);
  }

  private Project createProject(CreateProjectDTO data){
    TestUtilsMocks mocksObjects = new TestUtilsMocks();
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    User persistedUser = this.createUser(mockCreateUserDTO);
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