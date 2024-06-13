package com.orange.porfolio.orange.portfolio.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable = false, length = 30)
  private String name;

  @ManyToMany(mappedBy = "roles")
  @JsonIgnore
  private Collection<User> users = new HashSet<>();

  public Role(String name) {
    this.name = name;
  }
}
