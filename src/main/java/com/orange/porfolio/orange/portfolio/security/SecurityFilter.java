package com.orange.porfolio.orange.portfolio.security;

import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import com.orange.porfolio.orange.portfolio.services.UsersService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  private TokenService tokenService;
  private UsersRepository usersRepository;

  public SecurityFilter(TokenService tokenService, UsersRepository usersRepository) {
    this.tokenService = tokenService;
    this.usersRepository = usersRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(request.getRequestURI().equals("/auth/login") || request.getRequestURI().equals("/auth/register")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = this.tokenService.recoverToken(request);
    String userId = this.tokenService.validateToken(token);

    if(userId != null){
      User user = this.usersRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));
      var authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
      var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);

  }


}
