package com.dolsk.tyres.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private T data;
  private String message;;
}