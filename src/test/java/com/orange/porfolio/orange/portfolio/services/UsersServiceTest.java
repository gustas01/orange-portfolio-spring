package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.UpdateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersServiceTest {
  @Mock
  private UsersRepository usersRepository;

  private final ModelMapper mapper = new ModelMapper();
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private ImageUploadService imageUploadService;

  @InjectMocks
  private UsersService usersService;

  TestUtilsMocks mocksObjects;
  AutoCloseable autoCloseable;
  @BeforeEach
  void setup(){
    autoCloseable = MockitoAnnotations.openMocks(this);
    usersService = new UsersService(usersRepository, mapper, passwordEncoder, imageUploadService);
    mocksObjects = new TestUtilsMocks();
  }

  @Test
  @DisplayName("Should return a User by Id")
  void findOneSuccess() {
    User mockUser = mocksObjects.mockUser;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

    UserDTO response = usersService.findOne(mockUser.getId());

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(response.getAvatarUrl(), mockUser.getAvatarUrl());
    verify(usersRepository, times(1)).findById(mockUser.getId());
  }


  @Test
  @DisplayName("Should TRY to return a User by Id and throw an exception")
  void findOneFailure() {
    User mockUser = mocksObjects.mockUser;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () -> usersService.findOne(mockUser.getId()));

    assertEquals(exception.getMessage(), "Usuário não encontrado!");
    verify(usersRepository, times(1)).findById(mockUser.getId());
  }

  @Test
  @DisplayName("Should return a User by email")
  void findByEmailSuccess() {
    User mockUser = mocksObjects.mockUser;

    when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

    User response = usersService.findByEmail(mockUser.getEmail());

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getProjects(), mockUser.getProjects());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(response.getAvatarUrl(), mockUser.getAvatarUrl());
    verify(usersRepository, times(1)).findByEmail(mockUser.getEmail());
  }


  @Test
  @DisplayName("Should TRY to return a User by email and throw an exception")
  void findByEmailFailure() {
    User mockUser = mocksObjects.mockUser;

    when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () -> usersService.findByEmail(mockUser.getEmail()));

    assertEquals(exception.getMessage(), "Usuário não encontrado!");
    verify(usersRepository, times(1)).findByEmail(mockUser.getEmail());
  }

  @Test
  @DisplayName("Should update a User WITH a file")
  void updateSuccessWithFile() {
    User mockUser = mocksObjects.mockUser;
    UpdateUserDTO mockUpdateUserDTO = mocksObjects.mockUpdateUserDTO;
    MockMultipartFile mockMultipartFileImage = mocksObjects.mockMultipartFileImage;
    ImgurResponse mockImgurResponse = mocksObjects.mockImgurResponse;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(imageUploadService.uploadImage(mockMultipartFileImage)).thenReturn(mockImgurResponse);
    when(usersRepository.save(mockUser)).thenReturn(mockUser);

    UserDTO response = usersService.update(mockUser.getId(), mockUpdateUserDTO, mockMultipartFileImage);

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(response.getAvatarUrl(), mockUser.getAvatarUrl());

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(imageUploadService, times(1)).uploadImage(mockMultipartFileImage);
    verify(usersRepository, times(1)).save(mockUser);
  }


  @Test
  @DisplayName("Should update a User WITHOUT a file")
  void updateSuccessWithoutFile() {
    User mockUser = mocksObjects.mockUser;
    UpdateUserDTO mockUpdateUserDTO = mocksObjects.mockUpdateUserDTO;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    when(usersRepository.save(mockUser)).thenReturn(mockUser);

    UserDTO response = usersService.update(mockUser.getId(), mockUpdateUserDTO, null);

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(response.getAvatarUrl(), "");

    verify(usersRepository, times(1)).findById(mockUser.getId());
    verify(usersRepository, times(1)).save(mockUser);
  }


  @Test
  @DisplayName("Should TRY to update a User WITH file and throw and exception because File had a INCORRECT format")
  void updateFailureFileWithIncorrectFormat() {
    User mockUser = mocksObjects.mockUser;
    UpdateUserDTO mockUpdateUserDTO = mocksObjects.mockUpdateUserDTO;
    MockMultipartFile mockMultipartFileText = mocksObjects.mockMultipartFileText;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () ->  usersService.update(mockUser.getId(), mockUpdateUserDTO, mockMultipartFileText));

    assertEquals(exception.getMessage(), "Tipo de arquivo não suportado. Use arquivos .JPG ou .PNG");

    verify(usersRepository, times(1)).findById(mockUser.getId());
  }



  @Test
  @DisplayName("Should TRY to update a User and throw and exception because User doesn't exist")
  void updateFailureUserNotFound() {
    User mockUser = mocksObjects.mockUser;
    UpdateUserDTO mockUpdateUserDTO = mocksObjects.mockUpdateUserDTO;

    when(usersRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

    Exception exception = assertThrowsExactly(EntityNotFoundException.class, () ->  usersService.update(mockUser.getId(), mockUpdateUserDTO, null));

    assertEquals(exception.getMessage(), "Usuário não encontrado!");

    verify(usersRepository, times(1)).findById(mockUser.getId());
  }
}