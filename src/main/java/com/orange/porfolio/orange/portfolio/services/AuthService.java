package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;

import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
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

  public AuthService(UsersRepository usersRepository, ModelMapper mapper, PasswordEncoder passwordEncoder, TokenService tokenService) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

  public String login(@RequestBody LoginUserDTO loginUserDTO) {
    Optional<User> user = this.usersRepository.findByEmail(loginUserDTO.getEmail());
    if (user.isPresent() && passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
      return this.tokenService.generateToken(user.get());
    throw new BadRequestRuntimeException("Usuário ou senha inválidos!");
  }

  public User register(@RequestBody CreateUserDTO createUserDTO) throws BadRequestException {
//    String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%¨&*()_{}/^+=])(?=\\S+$).{8,200}$";
//    Pattern pattern = Pattern.compile(regex);
//    Matcher matcher = pattern.matcher(createUserDTO.getPassword());
//
//    if(!matcher.matches()){
//      return new ResponseEntity<>("A senha deve conter 1 letra maiúscula, 1 minúscula, 1 número e 1 símbolo pelo menos", HttpStatus.BAD_REQUEST);
//    }

    Optional<User> user = this.usersRepository.findByEmail(createUserDTO.getEmail());
    if (user.isPresent()) throw new BadRequestException("Usuário com esse email já existe!");

    User newUser = mapper.map(createUserDTO, User.class);
    newUser.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
    this.usersRepository.save(newUser);
    return newUser;
  }


}
