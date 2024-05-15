package com.orange.porfolio.orange.portfolio.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.entities.Role;
import com.orange.porfolio.orange.portfolio.entities.User;
import com.orange.porfolio.orange.portfolio.repositories.RoleRepository;
import com.orange.porfolio.orange.portfolio.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  private final TokenService tokenService;
  private final UsersRepository usersRepository;
  private final RoleRepository roleRepository;

  public SecurityFilter(TokenService tokenService, UsersRepository usersRepository,
                        RoleRepository roleRepository) {
    this.tokenService = tokenService;
    this.usersRepository = usersRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(request.getRequestURI().equals("/auth/login")
      || request.getRequestURI().equals("/auth/register")
      || request.getRequestURI().equals("/auth/login/google")
      || request.getRequestURI().equals("/auth/login/google")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = this.tokenService.recoverToken(request);

    //TODO: incluir no if a lógica para quando o token existir mas já tiver expirado
    if (token == null){
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json; charset=UTF-8");

      ObjectMapper mapper = new ObjectMapper();

      Map<String, Object> res = new HashMap<>();
      res.put("timestamp", LocalDateTime.now().toString());
      res.put("status", HttpStatus.UNAUTHORIZED.value());
      res.put("error", "Unauthorized");
      res.put("message", "Usuário não autenticado");

      String json = mapper.writeValueAsString(res);
      response.getWriter().write(json);
      return;
    }

    String userId = this.tokenService.validateToken(token);

    if(userId != null){
      User user = this.usersRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));

      List<Role> roles = roleRepository.findByUsersId(user.getId());

      List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
      for (Role role : roles) {
        grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
      }

      var authentication = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);

  }

}
