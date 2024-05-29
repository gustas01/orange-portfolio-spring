package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.ImgurResponse;
import com.orange.porfolio.orange.portfolio.DTOs.UpdateUserDTO;
import com.orange.porfolio.orange.portfolio.DTOs.UserDTO;
import com.orange.porfolio.orange.portfolio.config.ImageUploadService;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
  private final UsersRepository usersRepository;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final ImageUploadService imageUploadService;
  private final List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png");

  public UsersService(UsersRepository usersRepository, ModelMapper mapper, @Lazy PasswordEncoder passwordEncoder,
                      ImageUploadService imageUploadService) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
    this.imageUploadService = imageUploadService;
  }

  public UserDTO findOne(UUID userId) {
    Optional<User> user = this.usersRepository.findById(userId);
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

  public UserDTO update(UUID userId, UpdateUserDTO updateUserDTO, MultipartFile file){
    User user = this.usersRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));

    if (updateUserDTO.getEmail() != null) user.setEmail(updateUserDTO.getEmail());
    if (updateUserDTO.getFirstName() != null) user.setFirstName(updateUserDTO.getFirstName());
    if (updateUserDTO.getLastName() != null) user.setLastName(updateUserDTO.getLastName());
    if (updateUserDTO.getPassword() != null) user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));

    if(file != null){
      if (!allowedMimeTypes.contains(file.getContentType()))
        throw new BadRequestRuntimeException("Tipo de arquivo não suportado. Use arquivos .JPG ou .PNG");
      ImgurResponse imgurResponse = this.imageUploadService.uploadImage(file);
      user.setAvatarUrl(imgurResponse.getData().getLink());
    }

    this.usersRepository.save(user);
    return mapper.map(user, UserDTO.class);
  }
}
