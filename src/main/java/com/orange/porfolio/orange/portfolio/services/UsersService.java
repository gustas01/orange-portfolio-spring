package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
  private UsersRepository usersRepository;
  private ModelMapper mapper;
  private PasswordEncoder passwordEncoder;

  public UsersService(UsersRepository usersRepository, ModelMapper mapper, @Lazy PasswordEncoder passwordEncoder) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDTO findOne(UUID id) {
    Optional<User> user = this.usersRepository.findById(id);
    if (user.isEmpty())
      throw new EntityNotFoundException("Usuário não encontrado!");
    return mapper.map(user.get(), UserDTO.class);
  }

  public User findByEmail (String email) {
    Optional<User> user = this.usersRepository.findByEmail(email);
    if (user.isEmpty())
      throw new EntityNotFoundException("Usuário não encontrado!");
    return user.get();
  }

  public UserDTO update(UUID id, CreateUserDTO updateUserDTO){
    User user = this.usersRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));

    if (updateUserDTO.getEmail() != null) user.setEmail(updateUserDTO.getEmail());
    if (updateUserDTO.getFirstName() != null) user.setFirstName(updateUserDTO.getFirstName());
    if (updateUserDTO.getLastName() != null) user.setLastName(updateUserDTO.getLastName());
    if (updateUserDTO.getPassword() != null) user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));

    this.usersRepository.save(user);
    return mapper.map(user, UserDTO.class);
  }
}
