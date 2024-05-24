package com.orange.porfolio.orange.portfolio.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ImgurResponse {
//  private Integer status;
//  private Boolean success;
  private Data data;

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @AllArgsConstructor
  public static class Data{
    private String link;
  }
}
