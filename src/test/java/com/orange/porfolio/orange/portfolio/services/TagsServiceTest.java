package com.orange.porfolio.orange.portfolio.services;

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
  @DisplayName("Should return all tags")
  void findAll(){
    List<Tag> tags = mocksObjects.mockTags;

    when(tagsRepository.findAllByActive(true)).thenReturn(tags);

    List<Tag> response = tagsService.findAll();

    assertArrayEquals(response.toArray(), tags.toArray());
  }

}