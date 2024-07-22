package com.orange.porfolio.orange.portfolio.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
//  @JsonProperty("first_name")
  private String firstName;
  private String lastName;
  private String email;
  private String avatarUrl;
//  private List<ProjectDTO> projects = new ArrayList<>();
}
