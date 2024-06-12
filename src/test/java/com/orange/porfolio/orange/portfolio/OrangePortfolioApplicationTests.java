package com.orange.porfolio.orange.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.*;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrangePortfolioApplicationTests {
  String token;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UsersRepository userRepository;

  TestUtilsMocks mocksObjects;
  @BeforeEach
  void setup(){
    mocksObjects = new TestUtilsMocks();
  }

	@Test
  @DisplayName("Should create an user")
  @Order(1)
	void register() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String url = mocksObjects.mockUrl+port+"/auth/register";
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    MockMultipartFile mockMultipartFile = mocksObjects.mockMultipartFileImage;

    HttpHeaders dataHeaders = new HttpHeaders();
    HttpHeaders fileHeaders = new HttpHeaders();
    HttpHeaders formHeaders = new HttpHeaders();
    dataHeaders.setContentType(MediaType.APPLICATION_JSON);
    fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    formHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("data", new HttpEntity<>(mapper.writeValueAsString(mockCreateUserDTO), dataHeaders) );
    formData.add("image", new HttpEntity<>(mockMultipartFile.getBytes(), fileHeaders));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, formHeaders);

    ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(url, requestEntity, UserDTO.class);
    Optional<User> user = userRepository.findByEmail(response.getBody().getEmail());

    assertNotNull(user);
    assertEquals(mockCreateUserDTO.getFirstName(), user.get().getFirstName());
    assertEquals(mockCreateUserDTO.getLastName(), user.get().getLastName());
    assertEquals(mockCreateUserDTO.getEmail(), user.get().getEmail());
    assertEquals("201 CREATED", response.getStatusCode().toString());

	}


  @Test
  @DisplayName("Should login an user")
  @Order(2)
  void login() {
    LoginUserDTO loginUserDTO = mocksObjects.mockLoginUserDTO;
    String url = mocksObjects.mockUrl+port+"/auth/login";

    ResponseEntity<String> response = testRestTemplate.postForEntity(url, loginUserDTO, String.class);

    List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
    assert cookies != null;
    cookies = cookies.stream().filter(c -> c.startsWith("token=")).toList();

    assertEquals(1, cookies.size());
    this.token = cookies.getFirst().substring(6);

    assertNotNull(response);
    assertNotNull(this.token);
    assertEquals("Usuário logado com sucesso!", response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
  }

  @Test
  @DisplayName("Should make a request and throw an exception due to the user not being logged in")
  void requestFail(){
    String url = mocksObjects.mockUrl+port+"/me/data";

    ResponseEntity<StandardError> response = testRestTemplate.getForEntity(url, StandardError.class);

    assertEquals("Usuário não autenticado", response.getBody().getMessage());
    assertEquals("401 UNAUTHORIZED", response.getStatusCode().toString());
    assertEquals(401, response.getBody().getStatus());
  }


  @Test
  @DisplayName("Should TRY to create a Tag and throw an exception due to permission")
  void createTag(){
    Tag mockTag = mocksObjects.mockTag;
    String url = mocksObjects.mockUrl+port+"/tags";

    Cookie cookie = new Cookie("token", this.token);
    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token="+cookie.getValue());
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity entityWithCookies = new HttpEntity<>(mockTag, headersWithCookies);

    ResponseEntity<StandardError> response = testRestTemplate.exchange(url, HttpMethod.POST, entityWithCookies, StandardError.class);

    assertEquals("403 FORBIDDEN", response.getStatusCode().toString());
    assertEquals(403, Objects.requireNonNull(response.getBody()).getStatus());
    assertEquals("Você não tem permissão para acessar esse serviço, contate um administrador", response.getBody().getMessage());
  }

}
