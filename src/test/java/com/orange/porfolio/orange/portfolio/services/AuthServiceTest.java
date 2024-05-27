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
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

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
  @Mock
  private OAuth2AuthenticationToken mockOAuth2AuthenticationToken;

  @Autowired
  @InjectMocks
  private AuthService authService;

  AutoCloseable autoCloseable;
  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

//  @AfterEach
//  void finnaly() throws Exception {
//    this.autoCloseable.close();
//  }

  @Test
  @DisplayName("Should return a jwt token")
  void loginSuccess() {
    LoginUserDTO mockLoginUserDTO = mocksObjects.mockLoginUserDTO;
    User mockUser = mocksObjects.mockUser;
    String mockToken = mocksObjects.mockToken;

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
    LoginUserDTO mockLoginUserDTO = mocksObjects.mockLoginUserDTO;
    User mockUser = mocksObjects.mockUser;
    String mockToken = mocksObjects.mockToken;

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
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    User mockUser = mocksObjects.mockUser;
    UserDTO mockUserDTO = mocksObjects.mockUserDTO;
    Role mockRoleUser = mocksObjects.mockRoleUser;
    ImgurResponse mockImgurResponse =mocksObjects.mockImgurResponse;
    MockMultipartFile mockMultipartFileImage =mocksObjects.mockMultipartFileImage;

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

    verify(usersRepository, times(1)).findByEmail(mockCreateUserDTO.getEmail());
    verify(roleRepository, times(1)).findByName(mockRoleUser.getName());
    verify(mapper, times(1)).map(mockCreateUserDTO, User.class);
    verify(passwordEncoder, times(1)).encode(mockCreateUserDTO.getPassword());
    verify(imageUploadService, times(1)).uploadImage(mockMultipartFileImage);
    verify(usersRepository, times(1)).save(mockUser);
    verify(mapper, times(1)).map(mockUser, UserDTO.class);
  }


  @Test
  @DisplayName("Should try to register a user WITH an avatar WITH INCORRECT format")
  void registerSuccessWithAvatarWithIncorrectFormat() {
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    User mockUser = mocksObjects.mockUser;
    UserDTO mockUserDTO = mocksObjects.mockUserDTO;
    Role mockRoleUser = mocksObjects.mockRoleUser;
    ImgurResponse mockImgurResponse =mocksObjects.mockImgurResponse;
    MockMultipartFile mockMultipartFileText =mocksObjects.mockMultipartFileText;

    when(usersRepository.findByEmail(mockCreateUserDTO.getEmail())).thenReturn(Optional.empty());
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);
    when(passwordEncoder.encode(mockCreateUserDTO.getPassword())).thenReturn(mockUser.getPassword());
    when(usersRepository.save(mockUser)).thenReturn(mockUser);
    when(mapper.map(mockCreateUserDTO, User.class)).thenReturn(mockUser);
    when(mapper.map(mockUser, UserDTO.class)).thenReturn(mockUserDTO);

    when(imageUploadService.uploadImage(mockMultipartFileText)).thenReturn(mockImgurResponse);


    Exception exception = assertThrowsExactly(BadRequestRuntimeException.class, () -> authService.register(mockCreateUserDTO, mockMultipartFileText));
    assertEquals(exception.getMessage(), "Tipo de arquivo não suportado. Use arquivos .JPG ou .PNG");

    verify(usersRepository, times(1)).findByEmail(mockCreateUserDTO.getEmail());
    verify(roleRepository, times(1)).findByName(mockRoleUser.getName());
    verify(mapper, times(1)).map(mockCreateUserDTO, User.class);
    verify(passwordEncoder, times(1)).encode(mockCreateUserDTO.getPassword());
  }

  @Test
  @DisplayName("Should register a user WITHOUT an avatar")
  void registerSuccessWithoutAvatar() {
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    User mockUser = mocksObjects.mockUser;
    UserDTO mockUserDTO = mocksObjects.mockUserDTO;
    Role mockRoleUser = mocksObjects.mockRoleUser;

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

    verify(usersRepository, times(1)).findByEmail(mockCreateUserDTO.getEmail());
    verify(roleRepository, times(1)).findByName(mockRoleUser.getName());
    verify(mapper, times(1)).map(mockCreateUserDTO, User.class);
    verify(passwordEncoder, times(1)).encode(mockCreateUserDTO.getPassword());
    verify(usersRepository, times(1)).save(mockUser);
    verify(mapper, times(1)).map(mockUser, UserDTO.class);
  }

  @Test
  @DisplayName("Should login with google with a existing google user")
  void loginWithGoogleWithExistingUser() {
    User mockUser = mocksObjects.mockUser;
    Role mockRoleUser = mocksObjects.mockRoleUser;
    String mockToken = mocksObjects.mockToken;


    OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singletonList(() -> "user"),
            Map.of("given_name", mockUser.getFirstName(),
                  "family_name", mockUser.getLastName(),
                  "email", mockUser.getEmail(),
                  "name", mockUser.getFirstName()
            ), "name");

    when(mockOAuth2AuthenticationToken.getPrincipal()).thenReturn(oAuth2User);

    when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
    when(mapper.map(mockUser, User.class)).thenReturn(mockUser);
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);

    when(tokenService.generateToken(mockUser)).thenReturn(mockToken);
    String token = authService.loginWithGoogle(mockOAuth2AuthenticationToken);

    assertEquals(mockToken, token);
    verify(usersRepository, times(1)).findByEmail(mockUser.getEmail());
    verify(usersRepository, times(0)).save(mockUser);
    verify(mapper, times(1)).map(mockUser, User.class);
    verify(roleRepository, times(1)).findByName(mockRoleUser.getName());
    verify(tokenService, times(1)).generateToken(mockUser);
  }


  @Test
  @DisplayName("Should login with google with a NOT existing google user")
  void loginWithGoogleWithNotExistingUser() {
    User mockUser = mocksObjects.mockUser;
    Role mockRoleUser = mocksObjects.mockRoleUser;
    String mockToken = mocksObjects.mockToken;


    OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singletonList(() -> "user"),
            Map.of("given_name", mockUser.getFirstName(),
                    "family_name", mockUser.getLastName(),
                    "email", mockUser.getEmail(),
                    "name", mockUser.getFirstName()
            ), "name");

    when(mockOAuth2AuthenticationToken.getPrincipal()).thenReturn(oAuth2User);

    when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());
    when(mapper.map(mockUser, User.class)).thenReturn(mockUser);
    when(roleRepository.findByName(mockRoleUser.getName())).thenReturn(mockRoleUser);
    when(usersRepository.save(any(User.class))).thenReturn(mockUser);

    when(tokenService.generateToken(any(User.class))).thenReturn(mockToken);
    String token = authService.loginWithGoogle(mockOAuth2AuthenticationToken);

    assertEquals(mockToken, token);
    verify(usersRepository, times(1)).findByEmail(mockUser.getEmail());
    verify(usersRepository, times(1)).save(any(User.class));
    verify(mapper, times(0)).map(mockUser, User.class);
    verify(roleRepository, times(1)).findByName(mockRoleUser.getName());
    verify(tokenService, times(1)).generateToken(any(User.class));
  }
}