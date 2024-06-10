package com.orange.porfolio.orange.portfolio.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ImgurResponse {
//  private Integer status;
//  private Boolean success;
  private Data data;

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Data{
    private String link;
  }
}
