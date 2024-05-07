package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;

import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.RoleRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import com.orange.porfolio.orange.portfolio.security.TokenService;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class AuthService {
  private UsersRepository usersRepository;
  private ModelMapper mapper;
  private PasswordEncoder passwordEncoder;
  private TokenService tokenService;
  private RoleRepository roleRepository;

  public AuthService(UsersRepository usersRepository, ModelMapper mapper, PasswordEncoder passwordEncoder,
                     TokenService tokenService, RoleRepository roleRepository) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
  }

  public String login(@RequestBody LoginUserDTO loginUserDTO) {
    Optional<User> user = this.usersRepository.findByEmail(loginUserDTO.getEmail());
    if (user.isPresent() && passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
      return this.tokenService.generateToken(user.get());
    throw new BadRequestRuntimeException("Usuário ou senha inválidos!");
  }

  public UserDTO register(@RequestBody CreateUserDTO createUserDTO) throws BadRequestException {
    Optional<User> user = this.usersRepository.findByEmail(createUserDTO.getEmail());
    if (user.isPresent()) throw new BadRequestException("Usuário com esse email já existe!");

    Role role = this.roleRepository.findByName("user");

    User newUser = mapper.map(createUserDTO, User.class);
    newUser.getRoles().add(role);
    newUser.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
    this.usersRepository.save(newUser);
    return mapper.map(newUser, UserDTO.class) ;
  }


}
