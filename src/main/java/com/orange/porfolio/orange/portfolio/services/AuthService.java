package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.LoginUserDTO;

import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
  private final UsersRepository usersRepository;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final RoleRepository roleRepository;
  private final ImageUploadService imageUploadService;
  private final List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png");

  public AuthService(UsersRepository usersRepository, ModelMapper mapper, PasswordEncoder passwordEncoder,
                     TokenService tokenService, RoleRepository roleRepository,
                     ImageUploadService imageUploadService) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
    this.imageUploadService = imageUploadService;
  }

  public String login(LoginUserDTO loginUserDTO) {
    Optional<User> user = this.usersRepository.findByEmail(loginUserDTO.getEmail());
    if (user.isPresent() && passwordEncoder.matches(loginUserDTO.getPassword(), user.get().getPassword()))
      return this.tokenService.generateToken(user.get());
    throw new BadRequestRuntimeException("Usuário ou senha inválidos!");
  }

  public UserDTO register(CreateUserDTO createUserDTO, MultipartFile file) {
    Optional<User> user = this.usersRepository.findByEmail(createUserDTO.getEmail());
    if (user.isPresent()) {
      if (user.get().getGoogle())
        throw new BadRequestRuntimeException("Você já usou esse email criando uma conta usando o Google, tente logar dessa forma");
      throw new BadRequestRuntimeException("Usuário com esse email já existe!");
    }

    Role role = this.roleRepository.findByName("user");

    User newUser = mapper.map(createUserDTO, User.class);
    newUser.getRoles().add(role);
    newUser.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

    if(file != null){
      if (!allowedMimeTypes.contains(file.getContentType()))
        throw new BadRequestRuntimeException("Tipo de arquivo não suportado. User arquivos .JPG ou .PNG");
      ImgurResponse imgurResponse = this.imageUploadService.uploadImage(file);
      newUser.setAvatarUrl(imgurResponse.getData().getLink());
    }

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
