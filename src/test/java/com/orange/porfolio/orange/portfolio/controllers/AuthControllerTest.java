package com.orange.porfolio.orange.portfolio.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.TestUtilsMocks;
import com.orange.porfolio.orange.portfolio.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {
  @Mock
  private AuthService authService;
  @Mock
  HttpServletResponse httpServletResponse;
  @Mock
  Authentication authentication;
  @Mock
  OAuth2AuthenticationToken oAuth2AuthenticationToken;

  @Autowired
  @InjectMocks
  private AuthController authController;

  AutoCloseable autoCloseable;
  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void after() throws Exception {
    autoCloseable.close();
  }


  @Test
  @DisplayName("Should LOGIN and save a JWT Token in COOKIES of the response")
  void login() throws JsonProcessingException {
    LoginUserDTO mockLoginUserDTO = mocksObjects.mockLoginUserDTO;
    String mockToken = mocksObjects.mockToken;

    when(authService.login(mockLoginUserDTO)).thenReturn(mockToken);

    ResponseEntity<String> response = authController.login(mockLoginUserDTO, httpServletResponse);

    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(httpServletResponse).addCookie(cookieCaptor.capture());
    Cookie token = cookieCaptor.getValue();

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> res = new HashMap<>();
    res.put("message", "Usuário logado com sucesso!");
    String json = mapper.writeValueAsString(res);

    assertNotNull(token);
    assertEquals(json, response.getBody());
    assertEquals("token", token.getName());
    assertEquals(mockToken, token.getValue());
    verify(httpServletResponse, times(1)).addCookie(any(Cookie.class));
  }

  @Test
  @DisplayName("Should LOGIN WITH GOOGLE and save a JWT Token in COOKIES of the response")
  void loginGoole() throws JsonProcessingException {
    String mockToken = mocksObjects.mockToken;

    when(authService.loginWithGoogle(oAuth2AuthenticationToken)).thenReturn(mockToken);

    ResponseEntity<String> response = authController.loginGoole(oAuth2AuthenticationToken, httpServletResponse);

    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(httpServletResponse).addCookie(cookieCaptor.capture());
    Cookie token = cookieCaptor.getValue();

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> res = new HashMap<>();
    res.put("message", "Usuário logado com sucesso!");
    String json = mapper.writeValueAsString(res);

    assertNotNull(token);
    assertEquals(json, response.getBody());
    assertEquals("token", token.getName());
    assertEquals(mockToken, token.getValue());
    verify(httpServletResponse, times(1)).addCookie(any(Cookie.class));
  }
}