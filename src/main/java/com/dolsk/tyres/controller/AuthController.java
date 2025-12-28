package com.dolsk.tyres.controller;

import com.dolsk.tyres.dto.ApiResponse;
import com.dolsk.tyres.dto.AuthRequest;
import com.dolsk.tyres.dto.AuthResponse;
import com.dolsk.tyres.service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<AuthResponse>> signup(@Validated @RequestBody AuthRequest req) {
    AuthResponse resp = authService.signup(req);
    return ResponseEntity.ok(new ApiResponse<>(true, resp, null));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Validated @RequestBody AuthRequest req) {
    AuthResponse resp = authService.login(req);
    return ResponseEntity.ok(new ApiResponse<>(true, resp, null));
  }
}