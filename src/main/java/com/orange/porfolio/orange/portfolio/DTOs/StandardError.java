package com.orange.porfolio.orange.portfolio.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StandardError {
  private LocalDateTime timestamp;
  private Integer status;
  private String error;
  private String message;

  public StandardError(LocalDateTime timestamp, Integer status, String error, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
  }
}
