package com.orange.porfolio.orange.portfolio.DTOs;

import com.orange.porfolio.orange.portfolio.entities.Tag;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ProjectDTO {
  private UUID id;
  private String title;
  private String description;
  private String url;
  private String thumbnailUrl;
  private LocalDateTime createdAt;
  private Set<Tag> tags;
}
