package com.orange.porfolio.orange.portfolio.security;

import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.services.UsersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomUserDetailsService implements UserDetailsService {
  private UsersService usersService;

  public CustomUserDetailsService(UsersService usersService) {
    this.usersService = usersService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      User user = this.usersService.findByEmail(username);
      return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    } catch (EntityNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
