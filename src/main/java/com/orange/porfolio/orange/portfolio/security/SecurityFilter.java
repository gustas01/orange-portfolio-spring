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
    if (request.getRequestURI().equals("/auth/login")
            || request.getRequestURI().equals("/auth/register")
            || request.getRequestURI().equals("/auth/login/google")
            || request.getRequestURI().equals("/swagger-ui.html**")) {
      filterChain.doFilter(request, response);
      return;
    }

    //usando try/catch aqui porque o @ControllerAdvice não cobre aqui, pois aqui não passa por controller, e
    //uma exceção pode ser lançada na hora de validar o token
    try {
      String token = this.tokenService.recoverToken(request);
      String userId = this.tokenService.validateToken(token);

      if (userId != null) {
        User user = this.usersRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado!"));

        List<Role> roles = roleRepository.findByUsersId(user.getId());

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
          grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        var authentication = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (RuntimeException exception) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json; charset=UTF-8");

      ObjectMapper mapper = new ObjectMapper();

      Map<String, Object> res = new HashMap<>();
      res.put("timestamp", LocalDateTime.now().toString());
      res.put("status", HttpStatus.UNAUTHORIZED.value());
      res.put("error", "Unauthorized");
      res.put("message", exception.getMessage());

      String json = mapper.writeValueAsString(res);
      response.getWriter().write(json);
      return;
    }

    filterChain.doFilter(request, response);

  }

}
