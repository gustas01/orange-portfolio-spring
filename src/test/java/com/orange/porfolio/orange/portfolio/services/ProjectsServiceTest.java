package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.ProjectsRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectsServiceTest {
  @Mock
  private ProjectsRepository projectsRepository;
  @Mock
  private TagsService tagsService;
  @Mock
  private ModelMapper mapper;
  @Mock
  private UsersRepository usersRepository;
  @Mock
  private ImageUploadService imageUploadService;

  @Autowired
  @InjectMocks
  private ProjectsService projectsService;

  AutoCloseable autoCloseable;
  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should create a project WITH tag and WITH a thumbnail")
  void createProjectWithTagAndWithThumbnail() {
    User mockUser = mocksObjects.mockUser;
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;
    Project mockProject = mocksObjects.mockProject;
    ProjectDTO mockProjectDTO = mocksObjects.mockProjectDTO;
    ImgurResponse mockImgurResponse =mocksObjects.mockImgurResponse;
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;
    List<Tag> tags = mocksObjects.mockTags;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(mapper.map(mockCreateProjectDTO, Project.class)).thenReturn(mockProject);
    when(tagsService.findAll()).thenReturn(tags);
    when(imageUploadService.uploadImage(mockMultipartFileImage)).thenReturn(mockImgurResponse);
    when(mapper.map(mockProject, ProjectDTO.class)).thenReturn(mockProjectDTO);
    when(projectsRepository.save(mockProject)).thenReturn(mockProject);

    ProjectDTO response = projectsService.create(mockUser.getId(), mockCreateProjectDTO, mockMultipartFileImage);

    assertEquals(response.getId(), mockProject.getId());
    assertEquals(response.getTitle(), mockProject.getTitle());
    assertEquals(response.getDescription(), mockProject.getDescription());
    assertEquals(response.getUrl(), mockProject.getUrl());
    assertEquals(mockProject.getThumbnailUrl(), mockImgurResponse.getData().getLink());
    assertEquals(response.getTags(), mockProject.getTags());

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(mapper, times(1)).map(mockCreateProjectDTO, Project.class);
    verify(tagsService, times(1)).findAll();
    verify(imageUploadService, times(1)).uploadImage(mockMultipartFileImage);
    verify(mapper, times(1)).map(mockProject, ProjectDTO.class);
    verify(projectsRepository, times(1)).save(mockProject);
  }


  @Test
  @DisplayName("Should create a project WITH tag and WITHOUT a thumbnail")
  void createProjectWithTagAndWithoutThumbnail() {
    User mockUser = mocksObjects.mockUser;
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;
    Project mockProject = mocksObjects.mockProject;
    ProjectDTO mockProjectDTO = mocksObjects.mockProjectDTO;
    List<Tag> tags = mocksObjects.mockTags;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(mapper.map(mockCreateProjectDTO, Project.class)).thenReturn(mockProject);
    when(tagsService.findAll()).thenReturn(tags);
    when(mapper.map(mockProject, ProjectDTO.class)).thenReturn(mockProjectDTO);
    when(projectsRepository.save(mockProject)).thenReturn(mockProject);

    ProjectDTO response = projectsService.create(mockUser.getId(), mockCreateProjectDTO, null);

    assertEquals(response.getId(), mockProject.getId());
    assertEquals(response.getTitle(), mockProject.getTitle());
    assertEquals(response.getDescription(), mockProject.getDescription());
    assertEquals(response.getUrl(), mockProject.getUrl());
    assertEquals(response.getThumbnailUrl(), "");
    assertEquals(response.getTags(), mockProject.getTags());

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(mapper, times(1)).map(mockCreateProjectDTO, Project.class);
    verify(tagsService, times(1)).findAll();
    verify(mapper, times(1)).map(mockProject, ProjectDTO.class);
    verify(projectsRepository, times(1)).save(mockProject);
  }


  @Test
  @DisplayName("Should try create a project WITHOUT tag and throw and exception")
  void createProjectWithoutTag() {
    User mockUser = mocksObjects.mockUser;
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;
    Project mockProjectDTOWithoutTag = mocksObjects.mockProjectWithoutTag;
    ProjectDTO mockProjectDTO = mocksObjects.mockProjectDTO;
    List<Tag> tags = mocksObjects.mockTags;
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(mapper.map(mockCreateProjectDTO, Project.class)).thenReturn(mockProjectDTOWithoutTag);
    when(tagsService.findAll()).thenReturn(tags);
    when(mapper.map(mockProjectDTOWithoutTag, ProjectDTO.class)).thenReturn(mockProjectDTO);
    when(projectsRepository.save(mockProjectDTOWithoutTag)).thenReturn(mockProjectDTOWithoutTag);

    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () -> projectsService.create(mockUser.getId(), mockCreateProjectDTO, null));

    assertEquals(exception.getMessage(), "Tag inexistente");

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(mapper, times(1)).map(mockCreateProjectDTO, Project.class);
    verify(tagsService, times(1)).findAll();
    verify(imageUploadService, times(0)).uploadImage(mockMultipartFileImage);
    verify(mapper, times(0)).map(mockProjectDTOWithoutTag, ProjectDTO.class);
    verify(projectsRepository, times(0)).save(mockProjectDTOWithoutTag);
  }


  @Test
  @DisplayName("Should try create a project with an INVALID user and throw and exception")
  void createProjectWithInvalidToken() {
    User mockUser = mocksObjects.mockUser;
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;
    Project mockProjectDTOWithoutTag = mocksObjects.mockProjectWithoutTag;
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () -> projectsService.create(mockUser.getId(), mockCreateProjectDTO, null));

    assertEquals(exception.getMessage(), "Usuário não encontrado!");

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(mapper, times(0)).map(mockCreateProjectDTO, Project.class);
    verify(tagsService, times(0)).findAll();
    verify(imageUploadService, times(0)).uploadImage(mockMultipartFileImage);
    verify(mapper, times(0)).map(mockProjectDTOWithoutTag, ProjectDTO.class);
    verify(projectsRepository, times(0)).save(mockProjectDTOWithoutTag);
  }

  @Test
  @DisplayName("Should return a paginated list of projects")
  void discovery() {
    User mockUser = mocksObjects.mockUser;
    Project mockProject = mocksObjects.mockProject;

    Pageable pageable = PageRequest.of(0, 10);

    when(projectsRepository.findAllByAuthorIdNot(mockUser.getId(), pageable)).thenReturn(new PageImpl<>(List.of(mockProject), pageable, 1));
    Page<Project> projects = projectsService.discovery(mockUser.getId(), pageable);

    assertNotNull(projects);
    assertNotNull(projects.getContent());
    assertEquals(projects.getNumberOfElements(), 1);

    verify(projectsRepository, times(1)).findAllByAuthorIdNot(mockUser.getId(), pageable);
    assertEquals(projects.getContent(), List.of(mockProject) );
  }

  @Test
  @DisplayName("Should return one project by Id")
  void findOneSuccess() {
    Project mockProject = mocksObjects.mockProject;

    when(projectsRepository.findById(mockProject.getId())).thenReturn(Optional.of(mockProject));

    Project response = projectsService.findOne(mockProject.getId());

    assertEquals(response.getTitle(), mockProject.getTitle());
    assertEquals(response.getDescription(), mockProject.getDescription());
    assertEquals(response.getUrl(), mockProject.getUrl());
    assertEquals(response.getThumbnailUrl(), mockProject.getThumbnailUrl());
    assertEquals(response.getAuthor().getId(), mockProject.getAuthor().getId());
    verify(projectsRepository, times(1)).findById(mockProject.getId());
  }


  @Test
  @DisplayName("Should TRY to return one project by Id and throw an exception")
  void findOneFail() {
    Project mockProject = mocksObjects.mockProject;

    when(projectsRepository.findById(mockProject.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () -> projectsService.findOne(mockProject.getId()));

    assertEquals(exception.getMessage(), "Projeto não encontrado!");

    verify(projectsRepository, times(1)).findById(mockProject.getId());
  }

  @Test
  void findAllByAuthor() {
  }

  @Test
  void update() {
  }

  @Test
  void delete() {
  }
}