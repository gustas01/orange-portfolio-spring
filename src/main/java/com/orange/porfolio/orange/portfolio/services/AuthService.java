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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

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

  public String login(LoginUserDTO loginUserDTO) {
    Optional<User> user = this.usersRepository.findByEmail(loginUserDTO.getEmail());
    if (user.isPresent() && passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
      return this.tokenService.generateToken(user.get());
    throw new BadRequestRuntimeException("Usuário ou senha inválidos!");
  }

  public UserDTO register(CreateUserDTO createUserDTO) {
    Optional<User> user = this.usersRepository.findByEmail(createUserDTO.getEmail());
    if (user.isPresent()) {
      if (user.get().getGoogle())
        throw new BadRequestRuntimeException("Você já usou esse email criando uma conta usando o Google, tente logar dessa forma");
      throw new BadRequestRuntimeException("Usuário com esse email já existe!");
    }

    Role role = this.roleRepository.findByName("user");

    User newUser = mapper.map(createUserDTO, User.class);
    newUser.getRoles().add(role);
//    newUser.setGoogle(false);
    newUser.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
    this.usersRepository.save(newUser);
    return mapper.map(newUser, UserDTO.class) ;
  }

  public String loginWithGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
    String firstName = oAuth2AuthenticationToken.getPrincipal().getAttribute("given_name");
    String lastName = oAuth2AuthenticationToken.getPrincipal().getAttribute("family_name");
    String email = oAuth2AuthenticationToken.getPrincipal().getAttribute("email");

    Optional<User> user = this.usersRepository.findByEmail(email);
    User newUser = new User();

    if (user.isEmpty()){
      newUser.setFirstName(firstName);
      newUser.setLastName(lastName);
      newUser.setEmail(email);
      newUser.setPassword("");
      newUser.setPassword(passwordEncoder.encode(""));
      newUser.setGoogle(true);
      this.usersRepository.save(newUser);
    }else {
      newUser = mapper.map(user.get(), User.class);
    }

    Role role = this.roleRepository.findByName("user");

    newUser.getRoles().add(role);
    return this.tokenService.generateToken(newUser);
  }


}
