package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.security.JwtUtil;
import com.dolsk.tyres.dto.AuthRequest;
import com.dolsk.tyres.dto.AuthResponse;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtil;


  @Override
  public AuthResponse signup(AuthRequest req) {
    userRepo.findByUsername(req.getUsername())
            .ifPresent(u -> { throw new RuntimeException("Username already exists"); });

    User newUser = new User();
    newUser.setUsername(req.getUsername());
    newUser.setPassword(encoder.encode(req.getPassword()));
    newUser.setRole("ROLE_USER");
    userRepo.save(newUser);

    // Generate JWT using Spring Security UserDetails
    UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(newUser.getUsername())
            .password(newUser.getPassword())
            .roles("USER") // or newUser.getRole() if needed
            .build();

    return new AuthResponse(jwtUtil.generateToken(userDetails));
  }
  @Override
  public AuthResponse login(AuthRequest req) {
    // Check if user exists
    User user = userRepo.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));

    // Check if password matches
    if (!encoder.matches(req.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid username or password");
    }

    // Build UserDetails object for JWT
    UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles("USER") // or user.getRole() if you're managing roles dynamically
            .build();

    // Return token response
    return new AuthResponse(jwtUtil.generateToken(userDetails));
  }
}
