package com.orange.porfolio.orange.portfolio.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginUserDTO {
  @NotNull(message = "Email obrigatório!")
  @Email(message = "Email inválido!")
  private String email;

  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%¨&*()_{}/^+=])(?=\\S+$).{8,200}$",
          message = "A senha deve conter no mínimo 8 caracteres; sendo 1 letra maiúscula; 1 minúscula; 1 número e 1 símbolo pelo menos")
  private String password;
}
