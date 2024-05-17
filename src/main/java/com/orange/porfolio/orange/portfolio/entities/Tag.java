package com.orange.porfolio.orange.portfolio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 20, unique = true, nullable = false)
  @Setter
  private String tagName;

  @ManyToMany(mappedBy = "tags")
  @JsonIgnore
  private List<Project> projects = new ArrayList<>();

  @Setter
  @JsonIgnore
  private Boolean active = true;

  public Tag(String tagName) {
    this.tagName = tagName;
  }
}
