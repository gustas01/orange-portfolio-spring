package com.orange.porfolio.orange.portfolio.entities;

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
  private Integer id;

  @Column(unique = true, nullable = false, length = 30)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Collection<User> users = new HashSet<>();

  public Role(String name) {
    this.name = name;
  }
}
