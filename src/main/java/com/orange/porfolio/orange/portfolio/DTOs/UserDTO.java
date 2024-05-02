package com.orange.porfolio.orange.portfolio.DTOs;

import com.orange.porfolio.orange.portfolio.entities.Project;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String avatarUrl;
  private List<Project> projects = new ArrayList<>();
}
