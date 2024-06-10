package com.orange.porfolio.orange.portfolio.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClient {
  @Bean
  public OkHttpClient okHttp(){
    return new OkHttpClient();
  }
}
