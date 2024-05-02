package com.orange.porfolio.orange.portfolio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 30)
  @Setter
  private String title;

  @Column(nullable = false, length = 350)
  @Setter
  private String description;

  @Column(nullable = false)
  @Setter
  private String url;

  @Setter
  private String thumbnailUrl;

  @CreatedDate
  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToOne
  private User author;

  @ManyToMany(cascade = {CascadeType.ALL})
  private Set<Tag> tags = new HashSet<>();
}
