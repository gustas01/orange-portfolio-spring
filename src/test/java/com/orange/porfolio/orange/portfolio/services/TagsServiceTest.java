package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.TagDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.repositories.TagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class TagsServiceTest {
  @Mock
  private TagsRepository tagsRepository;
  @Mock
  private ModelMapper mapper;

  @Autowired
  @InjectMocks
  private TagsService tagsService;

  AutoCloseable autoCloseable;
  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return all active tags")
  void findAll(){
    List<Tag> mockTag = mocksObjects.mockTags;

    when(tagsRepository.findAllByActive(true)).thenReturn(mockTag);

    List<Tag> response = tagsService.findAll();

    assertArrayEquals(response.toArray(), mockTag.toArray());
  }


  @Test
  @DisplayName("Should create a tag")
  void createSuccess(){
    Tag mockTag = mocksObjects.mockTag;
    List<Tag> mockTags = mocksObjects.mockTags;
    TagDTO mockTagDTO = mocksObjects.mockTagDTO;
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;

    mockTag.setActive(false);
    when(tagsRepository.findOneByTagName(mockTag.getTagName())).thenReturn(Optional.of(mockTag));
    when(mapper.map(Optional.of(mockTag), TagDTO.class)).thenReturn(mockTagDTO);

    TagDTO response = tagsService.create(mockCreateTagDTO);

    assertEquals(response.getId(), mockTag.getId());
    assertEquals(response.getTagName(), mockTag.getTagName());
    verify(tagsRepository, times(1)).findOneByTagName(mockTag.getTagName());
    verify(mapper, times(1)).map(Optional.of(mockTag), TagDTO.class);
  }

}