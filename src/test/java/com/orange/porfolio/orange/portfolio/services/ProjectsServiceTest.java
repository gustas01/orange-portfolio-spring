package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateProjectDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.ProjectsRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
  @BeforeEach
  void setup(){
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should create a project WITH a thumbnail")
  void create() {
    User mockUser = TestUtilsMocks.mockUser;
    CreateProjectDTO mockCreateProjectDTO = TestUtilsMocks.mockCreateProjectDTO;
    Project mockProject = TestUtilsMocks.mockProject;
    ProjectDTO mockProjectDTO = TestUtilsMocks.mockProjectDTO;
    ImgurResponse mockImgurResponse =TestUtilsMocks.mockImgurResponse;
    MockMultipartFile mockMultipartFileImage = TestUtilsMocks.mockMultipartFileImage;
    List<Tag> tags = TestUtilsMocks.mockTags;

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
  void discovery() {
  }

  @Test
  void findOne() {
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