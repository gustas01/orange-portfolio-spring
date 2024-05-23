package com.orange.porfolio.orange.portfolio;

import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;
import com.orange.porfolio.orange.portfolio.entities.User;

import java.util.List;
import java.util.UUID;

public class TestUtilsMocks {
  public static String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

  public static LoginUserDTO mockLoginUserDTO = new LoginUserDTO("gustavo@email.com","12345678Aa!");
  public static User mockUser = new User(UUID.fromString("1a5b6e9f-a52c-44a8-9a9a-0d609065ca25"),
          "gustavo", "lima", "gustavo@email.com",
          "12345678Aa!", "", List.of(), List.of(), false);


//  public static
//  public static
//  public static
//  public static

}
