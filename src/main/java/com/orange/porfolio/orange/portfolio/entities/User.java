package com.orange.porfolio.orange.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(length = 30, nullable = false)
  @Setter
  private String firstName;

  @Column(length = 30, nullable = false)
  @Setter
  private String lastName;

  @Column(unique = true, nullable = false)
  @Setter
  private String email;

  @Column(nullable = false)
  @Setter
  private String password;

  @Setter
  private String avatarUrl;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
  private List<Project> projects = new ArrayList<>();

  @ManyToMany
  private Collection<Role> roles = new HashSet<>();

  @Column(nullable = false)
  @Setter
  private Boolean google = false;
}
