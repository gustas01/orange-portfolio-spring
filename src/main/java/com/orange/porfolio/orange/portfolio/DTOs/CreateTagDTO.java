package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateTagDTO {
  @NotNull(message = "O nome da Tag é obrigatório")
  @NotEmpty(message = "O nome da Tag não pode estar vazio")
  @Size(max = 20, message = "O nome da Tag deve ter no máximo {max} caracteres")
  private String tagName;
}
