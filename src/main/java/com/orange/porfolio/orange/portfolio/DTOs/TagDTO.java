package com.orange.porfolio.orange.portfolio.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TagDTO {
  private Integer id;
  private String tagName;
}
