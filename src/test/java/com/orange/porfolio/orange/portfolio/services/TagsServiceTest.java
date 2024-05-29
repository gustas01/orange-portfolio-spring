package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.TagDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.TagsRepository;
import jakarta.persistence.EntityNotFoundException;
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
  void createSuccessSetExistingTagToActive(){
    Tag mockTag = mocksObjects.mockTag;
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


  @Test
  @DisplayName("Should set a existing tag to active true")
  void createSuccessNewTag(){
    Tag mockTag = mocksObjects.mockTag;
    TagDTO mockTagDTO = mocksObjects.mockTagDTO;
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;

    mockTag.setActive(false);
    when(tagsRepository.findOneByTagName(mockTag.getTagName())).thenReturn(Optional.empty());
    when(tagsRepository.save(mockTag)).thenReturn(mockTag);
    when(mapper.map(mockCreateTagDTO, Tag.class)).thenReturn(mockTag);
    when(mapper.map(mockTag, TagDTO.class)).thenReturn(mockTagDTO);

    TagDTO response = tagsService.create(mockCreateTagDTO);

    assertEquals(response.getId(), mockTag.getId());
    assertEquals(response.getTagName(), mockTag.getTagName());
    verify(tagsRepository, times(1)).findOneByTagName(mockTag.getTagName());
    verify(mapper, times(1)).map(mockCreateTagDTO, Tag.class);
    verify(mapper, times(1)).map(mockTag, TagDTO.class);
  }


  @Test
  @DisplayName("Should TRY to create a new tag that already exist and it's active and THROW an exception")
  void createFailureNewTagAlreadyExist(){
    Tag mockTag = mocksObjects.mockTag;
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;

    when(tagsRepository.findOneByTagName(mockTag.getTagName())).thenReturn(Optional.of(mockTag));

    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class,() -> tagsService.create(mockCreateTagDTO)) ;

    assertEquals(exception.getMessage(), "Tag já existe!");

    verify(tagsRepository, times(1)).findOneByTagName(mockTag.getTagName());
    verify(mapper, times(0)).map(mockCreateTagDTO, Tag.class);
    verify(mapper, times(0)).map(mockTag, TagDTO.class);
  }


  @Test
  @DisplayName("Should set a tag to INACTIVE (delete)")
  void deleteSuccessTagSetToInactive(){
    Tag mockTag = mocksObjects.mockTag;

    when(tagsRepository.findById(mockTag.getId())).thenReturn(Optional.of(mockTag));

    String response = tagsService.delete(mockTag.getId());

    assertEquals(response, "Tag deletada com sucesso");
  }


  @Test
  @DisplayName("Should TRY to set a tag to INACTIVE (delete) and throw and exception")
  void deleteFailureTagSetToInactive(){
    Tag mockTag = mocksObjects.mockTag;

    when(tagsRepository.findById(mockTag.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class,() -> tagsService.delete(mockTag.getId())) ;

    assertEquals(exception.getMessage(), "Tag inexistente");
  }



  @Test
  @DisplayName("Should UPDATE a tag")
  void updateSuccess(){
    Tag mockTag = mocksObjects.mockTag;
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;

    when(tagsRepository.findById(mockTag.getId())).thenReturn(Optional.of(mockTag));

    String response = tagsService.update(mockTag.getId(), mockCreateTagDTO);

    assertEquals(response, "Tag atualizada com sucesso!");
    verify(tagsRepository, times(1)).findById(mockTag.getId());
  }


  @Test
  @DisplayName("Should TRY to update a tag and throw an exception because of empty tagName")
  void updateFailureEmptyTagname(){
    Tag mockTag = mocksObjects.mockTag;

    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () -> tagsService.update(mockTag.getId(), new CreateTagDTO(""))) ;

    assertEquals(exception.getMessage(), "Campos obrigatórios estão faltando");
  }


  @Test
  @DisplayName("Should TRY to update a tag and throw an exception because of non existing Tag")
  void updateFailureNotExistingTag(){
    Tag mockTag = mocksObjects.mockTag;
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;

    when(tagsRepository.findById(mockTag.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () -> tagsService.update(mockTag.getId(), mockCreateTagDTO)) ;

    assertEquals(exception.getMessage(), "Tag não encontrada!");
    verify(tagsRepository, times(1)).findById(mockTag.getId());
  }
}