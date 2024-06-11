package com.orange.porfolio.orange.portfolio;

import com.orange.porfolio.orange.portfolio.DTOs.*;
import com.orange.porfolio.orange.portfolio.entities.Project;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.entities.User;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.*;

public class TestUtilsMocks {
  public String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
  public LoginUserDTO mockLoginUserDTO = new LoginUserDTO("gustavo@email.com","12345678Aa!");
  public CreateUserDTO mockCreateUserDTO = new CreateUserDTO("gustavo", "lima", "gustavo@email.com", "12345678Aa!");
  public UpdateUserDTO mockUpdateUserDTO = new UpdateUserDTO("gustavo", "lima", "gustavo@email.com", "12345678Aa!");
  public User mockUser = new User(UUID.fromString("1a5b6e9f-a52c-44a8-9a9a-0d609065ca25"), "gustavo", "lima", "gustavo@email.com", "12345678Aa!", "", List.of(), new HashSet<>(), false);
  public UserDTO mockUserDTO = new UserDTO( "gustavo", "lima", "gustavo@email.com", "http://www.meuavatar.com", List.of());
  public Role mockRoleUser = new Role("user");
  public ImgurResponse mockImgurResponse = new ImgurResponse(new ImgurResponse.Data("http://uploadSuccess.com"));
  public MockMultipartFile mockMultipartFileImage = new MockMultipartFile("avatarImage", "myAvatar", "image/jpeg", new byte[] { 1 });
  public MockMultipartFile mockMultipartFileText =  new MockMultipartFile("avatarImage", "myAvatar", "text/txt", new byte[] { 1 });
  public Tag mockTag = new Tag(1, "backend", List.of(), true);
  public List<Tag> mockTags = List.of(mockTag);
  public TagDTO mockTagDTO = new TagDTO(1, "backend");
  public CreateTagDTO mockCreateTagDTO = new CreateTagDTO("backend");
  public Project mockProject = new Project(UUID.fromString("c9d7c521-5e4c-4e2a-b604-dac3d11bb5c0"), "Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br", "", LocalDateTime.now(), mockUser, new HashSet<>(Arrays.asList(mockTag)));
  public ProjectDTO mockProjectDTO = new ProjectDTO(UUID.fromString("c9d7c521-5e4c-4e2a-b604-dac3d11bb5c0"), "Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br", "", LocalDateTime.now(), new HashSet<>(Arrays.asList(mockTag)));
  public CreateProjectDTO mockCreateProjectDTO = new CreateProjectDTO("Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br");
  public UpdateProjectDTO mockUpdateProjectDTO = new UpdateProjectDTO("Um título de projeto update", "Uma descrição de projeto update", "http://www.umaurldeprojetoupdate.com.br");
  public Project mockProjectWithoutTag = new Project(UUID.fromString("c9d7c521-5e4c-4e2a-b604-dac3d11bb5c0"), "Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br", "", LocalDateTime.now(), mockUser, new HashSet<>());
  public ProjectDTO mockProjectDTOWithoutTag = new ProjectDTO(UUID.fromString("c9d7c521-5e4c-4e2a-b604-dac3d11bb5c0"), "Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br", "", LocalDateTime.now(),  new HashSet<>());
  public String mockUrl = "http://localhost:";
}
