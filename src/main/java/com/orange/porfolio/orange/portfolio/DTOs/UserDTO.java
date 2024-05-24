package com.orange.porfolio.orange.portfolio.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.porfolio.orange.portfolio.entities.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
//  @JsonProperty("first_name")
  private String firstName;
  private String lastName;
  private String email;
  private String avatarUrl;
  private List<Project> projects = new ArrayList<>();
}
