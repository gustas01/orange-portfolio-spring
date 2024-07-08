package com.orange.porfolio.orange.portfolio.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.orange.porfolio.orange.portfolio.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

@Service
public class TokenService {
  @Value("${api.security.token.secret}")
  private String jwtSecret;
  @Value("${api.security.token.expiration-hours}")
  private String jwtExpirationHours;

  public String generateToken(User user){
    try{
      Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

      String token = JWT.create().withIssuer("orange-portfolio")
              .withSubject(user.getId()+"").withExpiresAt(generateExpirationDate()).sign(algorithm);

      return token;
    }catch (JWTCreationException exception){
      throw new RuntimeException("Erro na autenticação");
    }
  }

  public String validateToken(String token){
    try {
      Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
      return JWT.require(algorithm).withIssuer("orange-portfolio")
              .build().verify(token).getSubject();
    }catch (JWTVerificationException exception){
      throw new JWTVerificationException("Usuário não autenticado");
    }
  }

  private Instant generateExpirationDate(){
    return LocalDateTime.now().plusHours(Long.parseLong(jwtExpirationHours)).toInstant(ZoneOffset.of("-03:00"));
  }

  public String recoverToken(HttpServletRequest request){
    if (request.getCookies() == null || Arrays.stream(request.getCookies()).noneMatch(c -> c.getName().equals("token"))) return null;
    return Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("token")).map(Cookie::getValue).findAny().map(Object::toString).get();
  }
}
