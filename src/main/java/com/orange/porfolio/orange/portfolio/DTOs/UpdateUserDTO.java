package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class UpdateUserDTO {
  @Size(max = 30, message = "O nome deve ter no máximo {max} caracteres")
  private String firstName;

  @Size(max = 30, message = "O sobrenome deve ter no máximo {max} caracteres")
  private String lastName;

  @Email(message = "Email inválido")
  private String email;

  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%¨&*()_{}/^+=])(?=\\S+$).{8,200}$",
          message = "A senha deve conter no mínimo 8 caracteres, sendo 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo pelo menos")
  private String password;
}
