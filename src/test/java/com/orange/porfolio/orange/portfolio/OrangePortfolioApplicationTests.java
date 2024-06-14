package com.orange.porfolio.orange.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.*;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.ProjectsRepository;
import com.orange.porfolio.orange.portfolio.repositories.TagsRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Profile("test")
class OrangePortfolioApplicationTests {
  String userToken;
  String adminToken;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UsersRepository userRepository;

  @Autowired
  private TagsRepository tagsRepository;

  @Autowired
  private ProjectsRepository projectsRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private PasswordEncoder passwordEncoder;

  TestUtilsMocks mocksObjects;

  @BeforeEach
  void setup() {
    mocksObjects = new TestUtilsMocks();
  }

  @BeforeAll
  void seed() {
    String passwordAdmin = passwordEncoder.encode("12345678Aa!");
    jdbcTemplate.execute("INSERT INTO roles (id, name) VALUES (2, 'admin')");
    jdbcTemplate.update("INSERT INTO users (id, email, first_name, last_name, password, google, avatar_url) VALUES ('68665ad3-e29a-4491-ae33-33a340813563', 'admin@admin.com', 'Admin', 'User', ?, false, null)", (passwordAdmin));
    jdbcTemplate.execute("INSERT INTO users_roles (users_id, roles_id) VALUES ('68665ad3-e29a-4491-ae33-33a340813563', 2);");
  }

  @Test
  @DisplayName("Should create an user")
  @Order(1)
  void register() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String url = mocksObjects.mockUrl + port + "/auth/register";
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;
    MockMultipartFile mockMultipartFile = mocksObjects.mockMultipartFileImage;

    HttpHeaders dataHeaders = new HttpHeaders();
    HttpHeaders fileHeaders = new HttpHeaders();
    HttpHeaders formHeaders = new HttpHeaders();
    dataHeaders.setContentType(MediaType.APPLICATION_JSON);
    fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    formHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("data", new HttpEntity<>(mapper.writeValueAsString(mockCreateUserDTO), dataHeaders));
    formData.add("image", new HttpEntity<>(mockMultipartFile.getBytes(), fileHeaders));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, formHeaders);

    ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(url, requestEntity, UserDTO.class);
    Optional<User> user = userRepository.findByEmail(Objects.requireNonNull(response.getBody()).getEmail());

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
    String url = mocksObjects.mockUrl + port + "/auth/login";

    ResponseEntity<String> response = testRestTemplate.postForEntity(url, loginUserDTO, String.class);

    List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
    assert cookies != null;
    cookies = cookies.stream().filter(c -> c.startsWith("token=")).toList();

    assertEquals(1, cookies.size());
    this.userToken = cookies.getFirst().substring(6);

    assertNotNull(response);
    assertNotNull(this.userToken);
    assertEquals("Usuário logado com sucesso!", response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
  }

  @Test
  @DisplayName("Should login an user as Admin")
  @Order(2)
  void loginAsAdmin() {
    String url = mocksObjects.mockUrl + port + "/auth/login";
    LoginUserDTO loginAdminDTO = new LoginUserDTO("admin@admin.com", "12345678Aa!");
    ResponseEntity<String> response = testRestTemplate.postForEntity(url, loginAdminDTO, String.class);

    List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
    assert cookies != null;
    cookies = cookies.stream().filter(c -> c.startsWith("token=")).toList();

    assertEquals(1, cookies.size());
    this.adminToken = cookies.getFirst().substring(6);

    assertNotNull(response);
    assertNotNull(this.adminToken);
    assertEquals("Usuário logado com sucesso!", response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
  }

  @Test
  @DisplayName("Should make a request and throw an exception due to the user not being logged in")
  @Order(3)
  void requestFail() {
    String url = mocksObjects.mockUrl + port + "/me/data";

    ResponseEntity<StandardError> response = testRestTemplate.getForEntity(url, StandardError.class);

    assertEquals("Usuário não autenticado", Objects.requireNonNull(response.getBody()).getMessage());
    assertEquals("401 UNAUTHORIZED", response.getStatusCode().toString());
    assertEquals(401, response.getBody().getStatus());
  }


  @Test
  @DisplayName("Should TRY to create a Tag and throw an exception due to permission")
  @Order(3)
  void createTagFailure() {
    Tag mockTag = mocksObjects.mockTag;
    String url = mocksObjects.mockUrl + port + "/tags";

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Tag> entityWithCookies = new HttpEntity<>(mockTag, headersWithCookies);

    ResponseEntity<StandardError> response = testRestTemplate.exchange(url, HttpMethod.POST, entityWithCookies, StandardError.class);

    assertEquals("403 FORBIDDEN", response.getStatusCode().toString());
    assertEquals(403, Objects.requireNonNull(response.getBody()).getStatus());
    assertEquals("Você não tem permissão para acessar esse serviço, contate um administrador", response.getBody().getMessage());
  }


  @Test
  @DisplayName("Should create a Tag logged as admin")
  @Order(3)
  void createTagAsAdmin() {
    CreateTagDTO mockCreateTagDTO = mocksObjects.mockCreateTagDTO;
    CreateTagDTO mockCreateTagDTO2 = mocksObjects.mockCreateTagDTO2;
    String url = mocksObjects.mockUrl + port + "/tags";

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + adminToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CreateTagDTO> entityWithCookies = new HttpEntity<>(mockCreateTagDTO, headersWithCookies);
    HttpEntity<CreateTagDTO> entityWithCookies2 = new HttpEntity<>(mockCreateTagDTO2, headersWithCookies);

    ResponseEntity<TagDTO> response = testRestTemplate.postForEntity(url, entityWithCookies, TagDTO.class);
    ResponseEntity<TagDTO> response2 = testRestTemplate.postForEntity(url, entityWithCookies2, TagDTO.class);

    assertEquals("201 CREATED", response.getStatusCode().toString());
    assertEquals(mockCreateTagDTO.getTagName(), Objects.requireNonNull(response.getBody()).getTagName());
    assertEquals("201 CREATED", response2.getStatusCode().toString());
    assertEquals(mockCreateTagDTO2.getTagName(), Objects.requireNonNull(response2.getBody()).getTagName());
  }


  @Test
  @DisplayName("Should TRY to update a Tag and throw an exception due to permission")
  @Order(4)
  void updateTagFailure() {
    String url = mocksObjects.mockUrl + port + "/tags/" + 2;

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    CreateTagDTO updateDataTag = new CreateTagDTO("frontend editado");

    HttpEntity<CreateTagDTO> entityWithCookies = new HttpEntity<>(updateDataTag, headersWithCookies);

    ResponseEntity<StandardError> response = testRestTemplate.exchange(url, HttpMethod.PUT, entityWithCookies, StandardError.class);

    assertEquals("403 FORBIDDEN", response.getStatusCode().toString());
    assertEquals(403, Objects.requireNonNull(response.getBody()).getStatus());
    assertEquals("Você não tem permissão para acessar esse serviço, contate um administrador", response.getBody().getMessage());
  }


  @Test
  @DisplayName("Should update a Tag logged as admin")
  @Order(4)
  void updateTagAsAdmin() {
    String url = mocksObjects.mockUrl + port + "/tags/" + 2;

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + adminToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    CreateTagDTO updateDataTag = new CreateTagDTO("frontend editado");

    HttpEntity<CreateTagDTO> entityWithCookies2 = new HttpEntity<>(updateDataTag, headersWithCookies);
    List<Tag> tags = tagsRepository.findAll();

    ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.PUT, entityWithCookies2, String.class);

    Tag updatedTag = tagsRepository.findAll().stream().filter(tag -> Objects.equals(tag.getId(), 2)).toList().getFirst();

    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals("Tag atualizada com sucesso!", Objects.requireNonNull(response.getBody()));
    assertEquals(updateDataTag.getTagName(), updatedTag.getTagName());
  }


  @Test
  @DisplayName("Should TRY to delete a Tag and throw an exception due to permission")
  @Order(6)
  void deleteTagFailure() {
    String url = mocksObjects.mockUrl + port + "/tags/" + 2;

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CreateTagDTO> entityWithCookies = new HttpEntity<>(headersWithCookies);

    ResponseEntity<StandardError> response = testRestTemplate.exchange(url, HttpMethod.DELETE, entityWithCookies, StandardError.class);

    assertEquals("403 FORBIDDEN", response.getStatusCode().toString());
    assertEquals(403, Objects.requireNonNull(response.getBody()).getStatus());
    assertEquals("Você não tem permissão para acessar esse serviço, contate um administrador", response.getBody().getMessage());
  }


  @Test
  @DisplayName("Should delete a Tag logged as admin")
  @Order(6)
  void deleteTagAsAdmin() {
    String url = mocksObjects.mockUrl + port + "/tags/" + 2;

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + adminToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> entityWithCookies = new HttpEntity<>(headersWithCookies);

    ResponseEntity<String> response = testRestTemplate.exchange(url, HttpMethod.DELETE, entityWithCookies, String.class);

    Tag updatedTag = tagsRepository.findAll().stream().filter(tag -> Objects.equals(tag.getId(), 2)).toList().getFirst();
    List<Tag> tags = tagsRepository.findAll();
    List<Tag> activeTags = tagsRepository.findAllByActive(true);

    assertEquals("204 NO_CONTENT", response.getStatusCode().toString());
    assertEquals(updatedTag.getActive(), false);
    assertEquals(tags.size(), 2);
    assertEquals(activeTags.size(), 1);
  }

  @Test
  @DisplayName("Should return the data of the logged user")
  @Order(3)
  void userData() {
    CreateUserDTO mockCreateUserDTO = mocksObjects.mockCreateUserDTO;

    String url = mocksObjects.mockUrl + port + "/users/me/data";

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> entityWithCookies = new HttpEntity<>(headersWithCookies);

    ResponseEntity<UserDTO> response = testRestTemplate.exchange(url, HttpMethod.GET, entityWithCookies, UserDTO.class);

    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(mockCreateUserDTO.getEmail(), Objects.requireNonNull(response.getBody()).getEmail());
    assertEquals(mockCreateUserDTO.getFirstName(), response.getBody().getFirstName());
    assertEquals(mockCreateUserDTO.getLastName(), response.getBody().getLastName());
  }


  @Test
  @DisplayName("Should update the logged user")
  @Order(5)
  void userUpdate() throws JsonProcessingException {
    UpdateUserDTO mockUpdateUserDTO = new UpdateUserDTO("gustavo editado", "lima", "gustavo@email.com", "12345678Aa!");

    ObjectMapper mapper = new ObjectMapper();

    String url = mocksObjects.mockUrl + port + "/users";

    HttpHeaders dataHeaders = new HttpHeaders();
    HttpHeaders formHeaders = new HttpHeaders();
    dataHeaders.setContentType(MediaType.APPLICATION_JSON);
    formHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    formHeaders.set(HttpHeaders.COOKIE, "token=" + userToken);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    HttpEntity<MultiValueMap<String, Object>> entityWithCookies = new HttpEntity<>(formData, formHeaders);

    formData.add("data", new HttpEntity<>(mapper.writeValueAsString(mockUpdateUserDTO), dataHeaders));

    ResponseEntity<UserDTO> response = testRestTemplate.exchange(url, HttpMethod.PUT, entityWithCookies, UserDTO.class);

    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(mockUpdateUserDTO.getEmail(), Objects.requireNonNull(response.getBody()).getEmail());
    assertEquals(mockUpdateUserDTO.getFirstName(), response.getBody().getFirstName());
    assertEquals(mockUpdateUserDTO.getLastName(), response.getBody().getLastName());
  }


  @Test
  @DisplayName("Should return the data of the user by id")
  @Order(3)
  void findOneUser() {
    CreateUserDTO createUserDTO = mocksObjects.mockCreateUserDTO;
    Optional<User> user = userRepository.findByEmail(createUserDTO.getEmail());

    assertTrue(user.isPresent());

    String url = mocksObjects.mockUrl + port + "/users/" + user.get().getId();

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> entityWithCookies = new HttpEntity<>(headersWithCookies);

    ResponseEntity<UserDTO> response = testRestTemplate.exchange(url, HttpMethod.GET, entityWithCookies, UserDTO.class);

    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(createUserDTO.getEmail(), Objects.requireNonNull(response.getBody()).getEmail());
    assertEquals(createUserDTO.getFirstName(), response.getBody().getFirstName());
    assertEquals(createUserDTO.getLastName(), response.getBody().getLastName());
  }


  @Test
  @DisplayName("Should create a project")
  @Order(3)
  void createProject() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String url = mocksObjects.mockUrl + port + "/projects";
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;
    CreateProjectDTO mockCreateProjectDTO2 = mocksObjects.mockCreateProjectDTO2;
//    MockMultipartFile mockMultipartFile = mocksObjects.mockMultipartFileImage;
    mockCreateProjectDTO.getTags().add("backend");
    mockCreateProjectDTO2.getTags().add("backend");

    HttpHeaders dataHeaders = new HttpHeaders();
//    HttpHeaders fileHeaders = new HttpHeaders();
    HttpHeaders formHeaders = new HttpHeaders();
    dataHeaders.setContentType(MediaType.APPLICATION_JSON);
//    fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    formHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    formHeaders.set(HttpHeaders.COOKIE, "token=" + userToken);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    MultiValueMap<String, Object> formData2 = new LinkedMultiValueMap<>();
    formData.add("data", new HttpEntity<>(mapper.writeValueAsString(mockCreateProjectDTO), dataHeaders));
    formData2.add("data", new HttpEntity<>(mapper.writeValueAsString(mockCreateProjectDTO2), dataHeaders));
//    formData.add("image", new HttpEntity<>(mockMultipartFile.getBytes(), fileHeaders));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, formHeaders);
    HttpEntity<MultiValueMap<String, Object>> requestEntity2 = new HttpEntity<>(formData2, formHeaders);

    ResponseEntity<ProjectDTO> response = testRestTemplate.postForEntity(url, requestEntity, ProjectDTO.class);
    ResponseEntity<ProjectDTO> response2 = testRestTemplate.postForEntity(url, requestEntity2, ProjectDTO.class);
//    Optional<ProjectDTO> projectDTO = userRepository.findByEmail(Objects.requireNonNull(response.getBody()).getEmail());

    assertNotNull(response.getBody());
    assertEquals("201 CREATED", response.getStatusCode().toString());
    assertEquals(mockCreateProjectDTO.getTitle(), response.getBody().getTitle());
    assertEquals(mockCreateProjectDTO.getDescription(), response.getBody().getDescription());
    assertEquals(mockCreateProjectDTO.getUrl(), response.getBody().getUrl());

    assertNotNull(response2.getBody());
    assertEquals("201 CREATED", response2.getStatusCode().toString());
    assertEquals(mockCreateProjectDTO2.getTitle(), response2.getBody().getTitle());
    assertEquals(mockCreateProjectDTO2.getDescription(), response2.getBody().getDescription());
    assertEquals(mockCreateProjectDTO2.getUrl(), response2.getBody().getUrl());

  }


  @Test
  @DisplayName("Should list the project that aren't from the logged user")
  @Order(4)
  void discoveryProjects() {

    String url = mocksObjects.mockUrl + port + "/projects/discovery";

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + adminToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headersWithCookies);

    ResponseEntity<CustomPageImpl<Project>> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<CustomPageImpl<Project>>() {
    });

    assertNotNull(response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(2, response.getBody().get().count());
    assertEquals(2, response.getBody().getTotalElements());
    assertEquals(1, response.getBody().getTotalPages());
    assertEquals("UNSORTED", response.getBody().getSort().toString());
    assertEquals(PageRequest.class, response.getBody().getPageable().getClass());

  }


  @Test
  @DisplayName("Should return one Project by Id")
  @Order(4)
  void finOneById() {
    CreateProjectDTO mockCreateProjectDTO = mocksObjects.mockCreateProjectDTO;

    String url = mocksObjects.mockUrl + port + "/projects/" + projectsRepository.findAll().getFirst().getId();

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headersWithCookies);

    ResponseEntity<Project> response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity, Project.class);

    assertNotNull(response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(mockCreateProjectDTO.getTitle(), response.getBody().getTitle());
    assertEquals(mockCreateProjectDTO.getDescription(), response.getBody().getDescription());
    assertEquals(mockCreateProjectDTO.getUrl(), response.getBody().getUrl());

  }


  @Test
  @DisplayName("Should update a project")
  @Order(5)
  void updateProject() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    String url = mocksObjects.mockUrl + port + "/projects/" + projectsRepository.findAll().getFirst().getId();

    CreateProjectDTO mockCreateProjectDTO2 = mocksObjects.mockCreateProjectDTO2;
//    MockMultipartFile mockMultipartFile = mocksObjects.mockMultipartFileImage;
    mockCreateProjectDTO2.getTags().add("backend");

    HttpHeaders dataHeaders = new HttpHeaders();
//    HttpHeaders fileHeaders = new HttpHeaders();
    HttpHeaders formHeaders = new HttpHeaders();
    dataHeaders.setContentType(MediaType.APPLICATION_JSON);
//    fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    formHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    formHeaders.set(HttpHeaders.COOKIE, "token=" + userToken);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("data", new HttpEntity<>(mapper.writeValueAsString(mockCreateProjectDTO2), dataHeaders));
//    formData.add("image", new HttpEntity<>(mockMultipartFile.getBytes(), fileHeaders));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, formHeaders);

    ResponseEntity<ProjectDTO> response = testRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, ProjectDTO.class);
//    Optional<ProjectDTO> projectDTO = userRepository.findByEmail(Objects.requireNonNull(response.getBody()).getEmail());

    assertNotNull(response.getBody());
    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals(mockCreateProjectDTO2.getTitle(), response.getBody().getTitle());
    assertEquals(mockCreateProjectDTO2.getDescription(), response.getBody().getDescription());
    assertEquals(mockCreateProjectDTO2.getUrl(), response.getBody().getUrl());

  }


  @Test
  @DisplayName("Should delete one Project by Id")
  @Order(6)
  void deleteProject() {
    UUID projectId = projectsRepository.findAll().getFirst().getId();
    String url = mocksObjects.mockUrl + port + "/projects/" + projectId;

    HttpHeaders headersWithCookies = new HttpHeaders();
    headersWithCookies.set(HttpHeaders.COOKIE, "token=" + userToken);
    headersWithCookies.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headersWithCookies);

    ResponseEntity<Project> response = testRestTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Project.class);


    assertEquals("204 NO_CONTENT", response.getStatusCode().toString());
    assertTrue(projectsRepository.findById(projectId).isEmpty());
  }
}
