package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UpdateProjectDTO {
  @Size(min = 3, max = 30, message = "O título deve ter entre {min} e {max} caracteres")
  private String title;

  @Size(min = 3, max = 350, message = "A descrição deve ter entre {min} e {max} caracteres")
  private String description;

  @URL(message = "URL inválida")
  private String url;
  
  @Size(min = 1, message = "Selecione pelo menos uma tag para o projeto")
  @ElementCollection
  private final List<String> tags = new ArrayList<>();
}
