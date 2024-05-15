package com.orange.porfolio.orange.portfolio.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private SecurityFilter securityFilter;
  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String google_client_id;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String google_client_secret;


  public SecurityConfig(SecurityFilter securityFilter) {
    this.securityFilter = securityFilter;
  }




  @Bean
  @Order(1)
  public SecurityFilterChain googleSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf
//                    .ignoringRequestMatchers("/auth/login/google")
                    .disable())
//      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .securityMatcher("/auth/login/google")
      .authorizeHttpRequests(authorize -> authorize
              .requestMatchers(AntPathRequestMatcher.antMatcher("/auth/login/google")).authenticated()
              .anyRequest().permitAll()
      )
      .oauth2Login(Customizer.withDefaults());
    return http.build();
  }

    @Bean
    @Order(2)
    public SecurityFilterChain JWTSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(csrf -> csrf.disable())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(authorize -> authorize
                      .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                      .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/users/me/data").hasAuthority("admin")
                    .requestMatchers(HttpMethod.GET, "/auth/login/google").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/oauth2/authorization/google").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/login").permitAll()
                      .anyRequest().authenticated()
              )
              .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
    }


  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

//  private ClientRegistration googleClientRegistration() {
//    return ClientRegistration.withRegistrationId("google")
//            .clientId(google_client_id)
//            .clientSecret(google_client_secret)
//            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//            .redirectUri("http://localhost:8080/login/oauth2/code/google")
////            .redirectUri("http://localhost:8080/auth/login/google/callback")
//            .scope("profile", "email")
////            .authorizationUri("http://localhost:8080")
//            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//            .tokenUri("https://www.googleapis.com/oauth2/v4/token")
//            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//            .userNameAttributeName(IdTokenClaimNames.SUB)
//            .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
//            .clientName("Google")
//            .build();
//  }
//
//  @Bean
//  public ClientRegistrationRepository clientRegistrationRepository() {
//    return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//  }
}
