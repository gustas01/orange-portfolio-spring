package com.orange.porfolio.orange.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrangePortfolioApplicationTests {

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
	void login() throws IOException {
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

}
