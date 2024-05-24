package com.orange.porfolio.orange.portfolio;

import com.orange.porfolio.orange.portfolio.DTOs.*;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.User;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class TestUtilsMocks {
  public static String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
  public static LoginUserDTO mockLoginUserDTO = new LoginUserDTO("gustavo@email.com","12345678Aa!");
  public static CreateUserDTO mockCreateUserDTO = new CreateUserDTO("gustavo", "lima", "gustavo@email.com", "12345678Aa!");
  public static User mockUser = new User(UUID.fromString("1a5b6e9f-a52c-44a8-9a9a-0d609065ca25"), "gustavo", "lima", "gustavo@email.com", "12345678Aa!", "", List.of(), new HashSet<>(), false);
  public static CreateProjectDTO mockCreateProjectDTO = new CreateProjectDTO("Um título de projeto", "Uma descrição de projeto", "http://www.umaurldeprojeto.com.br");
  public static Role mockRoleUser = new Role("user");
  public static UserDTO mockUserDTO = new UserDTO( "gustavo", "lima", "gustavo@email.com", "http://www.meuavatar.com", List.of());
  public static ImgurResponse mockImgurResponse = new ImgurResponse(new ImgurResponse.Data("http://uploadSuccess.com"));
  public static MockMultipartFile mockMultipartFileImage = new MockMultipartFile("avatarImage", "myAvatar", "image/jpeg", new byte[] { 1 });
  public static MockMultipartFile mockMultipartFileText =  new MockMultipartFile("avatarImage", "myAvatar", "text/txt", new byte[] { 1 });

}
