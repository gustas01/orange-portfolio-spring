package com.orange.porfolio.orange.portfolio.DTOs;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateProjectDTO {
  private String title;
  private String description;
  private String url;
  private List<String> tags;
}
