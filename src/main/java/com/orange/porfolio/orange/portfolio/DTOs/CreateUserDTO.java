package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CreateUserDTO {
  @NotNull(message = "O nome é obrigatório")
  @NotEmpty(message = "O nome não pode estar vazio")
  @Size(max = 30, message = "O nome deve ter no máximo 30 caracteres")
  private String firstName;

  @NotNull(message = "O sobrenome é obrigatório")
  @NotEmpty(message = "O sobrenome não pode estar vazio")
  @Size(max = 30, message = "O sobrenome deve ter no máximo 30 caracteres")
  private String lastName;

  @Email(message = "Email inválido")
  @NotNull(message = "O sobrenome é obrigatório")
  private String email;

  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%¨&*()_{}/^+=])(?=\\S+$).{8,200}$",
          message = "A senha deve conter no mínimo 8 caracteres, sendo 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo pelo menos")
  @NotNull(message = "A senha é obrigatória")
  private String password;
}
