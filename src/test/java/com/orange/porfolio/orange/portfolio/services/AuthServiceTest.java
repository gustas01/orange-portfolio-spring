package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.RoleRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import com.orange.porfolio.orange.portfolio.security.TokenService;
import org.assertj.core.internal.Bytes;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
  @Mock
  private UsersRepository usersRepository;
  @Mock
  private ModelMapper mapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private TokenService tokenService;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private ImageUploadService imageUploadService;

  @Autowired
  @InjectMocks
  private AuthService authService;

  AutoCloseable autoCloseable;
  @BeforeEach
  void setup(){
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

//  @AfterEach
//  void finnaly() throws Exception {
//    this.autoCloseable.close();
//  }

  @Test
  @DisplayName("Should return a jwt token")
  void loginSuccess() {
    LoginUserDTO mockLoginUserDTO = TestUtilsMocks.mockLoginUserDTO;
    User mockUser = TestUtilsMocks.mockUser;
    String mockToken = TestUtilsMocks.mockToken;

    when(usersRepository.findByEmail(mockLoginUserDTO.getEmail())).thenReturn(Optional.of(mockUser));
    when(tokenService.generateToken(mockUser)).thenReturn(mockToken);
    when(passwordEncoder.matches(mockLoginUserDTO.getPassword(), mockUser.getPassword())).thenReturn(true);
    String token = this.authService.login(mockLoginUserDTO);

    verify(usersRepository, times(1)).findByEmail(mockLoginUserDTO.getEmail());
    verify(tokenService, times(1)).generateToken(mockUser);
    verify(passwordEncoder, times(1)).matches(mockLoginUserDTO.getPassword(), mockUser.getPassword());
    assertEquals(mockToken, token);
  }

  @Test
  @DisplayName("Should throw an BadRequestRuntimeException")
  void loginFailure() {
    LoginUserDTO mockLoginUserDTO = TestUtilsMocks.mockLoginUserDTO;
    User mockUser = TestUtilsMocks.mockUser;
    String mockToken = TestUtilsMocks.mockToken;

    when(usersRepository.findByEmail(mockLoginUserDTO.getEmail())).thenReturn(Optional.of(mockUser));
    when(tokenService.generateToken(mockUser)).thenReturn(mockToken);
    when(passwordEncoder.matches(mockLoginUserDTO.getPassword(), mockUser.getPassword())).thenReturn(false);


    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () -> authService.login(mockLoginUserDTO));

    assertEquals(exception.getMessage(), "Usuário ou senha inválidos!");

    verify(usersRepository, times(1)).findByEmail(mockLoginUserDTO.getEmail());
    verify(passwordEncoder, times(1)).matches(mockLoginUserDTO.getPassword(), mockUser.getPassword());
  }

  @Test
  @DisplayName("Should register a user WITH an avatar WITH CORRECT format")
  void registerSuccessWithAvatarWithCorrectFormat() {
    CreateUserDTO mockCreateUserDTO = TestUtilsMocks.mockCreateUserDTO;
    User mockUser = TestUtilsMocks.mockUser;
    UserDTO mockUserDTO = TestUtilsMocks.mockUserDTO;
    Role mockRoleUser = TestUtilsMocks.mockRoleUser;
    ImgurResponse mockImgurResponse =TestUtilsMocks.mockImgurResponse;
    MockMultipartFile mockMultipartFileImage =TestUtilsMocks.mockMultipartFileImage;

    when(usersRepository.findByEmail(mockCreateUserDTO.getEmail())).thenReturn(Optional.empty());
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);
    when(passwordEncoder.encode(mockCreateUserDTO.getPassword())).thenReturn(mockUser.getPassword());
    when(usersRepository.save(mockUser)).thenReturn(mockUser);
    when(mapper.map(mockCreateUserDTO, User.class)).thenReturn(mockUser);
    when(mapper.map(mockUser, UserDTO.class)).thenReturn(mockUserDTO);

    when(imageUploadService.uploadImage(mockMultipartFileImage)).thenReturn(mockImgurResponse);
    UserDTO response = authService.register(mockCreateUserDTO, mockMultipartFileImage);

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(mockUser.getAvatarUrl(), mockImgurResponse.getData().getLink());
    assertEquals(response.getProjects(), mockUser.getProjects());
  }


  @Test
  @DisplayName("Should register a user WITH an avatar WITH INCORRECT format")
  void registerSuccessWithAvatarWithIncorrectFormat() {
    CreateUserDTO mockCreateUserDTO = TestUtilsMocks.mockCreateUserDTO;
    User mockUser = TestUtilsMocks.mockUser;
    UserDTO mockUserDTO = TestUtilsMocks.mockUserDTO;
    Role mockRoleUser = TestUtilsMocks.mockRoleUser;
    ImgurResponse mockImgurResponse =TestUtilsMocks.mockImgurResponse;
    MockMultipartFile mockMultipartFileText =TestUtilsMocks.mockMultipartFileText;

    when(usersRepository.findByEmail(mockCreateUserDTO.getEmail())).thenReturn(Optional.empty());
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);
    when(passwordEncoder.encode(mockCreateUserDTO.getPassword())).thenReturn(mockUser.getPassword());
    when(usersRepository.save(mockUser)).thenReturn(mockUser);
    when(mapper.map(mockCreateUserDTO, User.class)).thenReturn(mockUser);
    when(mapper.map(mockUser, UserDTO.class)).thenReturn(mockUserDTO);

    when(imageUploadService.uploadImage(mockMultipartFileText)).thenReturn(mockImgurResponse);

    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () -> authService.register(mockCreateUserDTO, mockMultipartFileText));
    assertEquals(exception.getMessage(), "Tipo de arquivo não suportado. Use arquivos .JPG ou .PNG");
  }

  @Test
  @DisplayName("Should register a user WITHOUT an avatar")
  void registerSuccessWithoutAvatar() {
    CreateUserDTO mockCreateUserDTO = TestUtilsMocks.mockCreateUserDTO;
    User mockUser = TestUtilsMocks.mockUser;
    UserDTO mockUserDTO = TestUtilsMocks.mockUserDTO;
    Role mockRoleUser = TestUtilsMocks.mockRoleUser;
    ImgurResponse mockImgurResponse =TestUtilsMocks.mockImgurResponse;

    when(usersRepository.findByEmail(mockCreateUserDTO.getEmail())).thenReturn(Optional.empty());
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);
    when(passwordEncoder.encode(mockCreateUserDTO.getPassword())).thenReturn(mockUser.getPassword());
    when(usersRepository.save(mockUser)).thenReturn(mockUser);
    when(mapper.map(mockCreateUserDTO, User.class)).thenReturn(mockUser);
    when(mapper.map(mockUser, UserDTO.class)).thenReturn(mockUserDTO);

    UserDTO response = authService.register(mockCreateUserDTO, null);

    assertEquals(response.getFirstName(), mockUser.getFirstName());
    assertEquals(response.getLastName(), mockUser.getLastName());
    assertEquals(response.getEmail(), mockUser.getEmail());
    assertEquals(mockUser.getAvatarUrl(), "");
    assertEquals(response.getProjects(), mockUser.getProjects());
  }

  @Test
  void loginWithGoogle() {
  }
}