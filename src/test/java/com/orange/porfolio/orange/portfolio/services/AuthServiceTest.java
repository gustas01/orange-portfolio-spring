package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
  void loginCase1() {
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
  void loginCase2() {
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
  void register() {
  }

  @Test
  void loginWithGoogle() {
  }
}