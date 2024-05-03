package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Array;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateProjectDTO {
  @NotNull(message = "Título obrigatório")
  @NotEmpty(message = "Título não pode estar vazio")
  @Size(max = 30, message = "O título deve ter no máximo 30 caracteres")
  private String title;

  @NotNull(message = "Descrição obrigatória")
  @Size(max = 350, message = "A descrição deve ter no máximo 350 caracteres")
  private String description;

  @NotNull(message = "Url obrigatória")
  @URL(message = "URL inválida")
  private String url;

  @NotEmpty(message = "Selecione pelo menos uma tag para o projeto")
  @ElementCollection
  private final List<String> tags = new ArrayList<>();
}
