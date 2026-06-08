package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.AuthRequest;
import com.dolsk.tyres.dto.AuthResponse;
import com.dolsk.tyres.exception.DuplicateFoundException;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.security.CustomUserDetailsService;
import com.dolsk.tyres.security.JwtUtil;
import com.dolsk.tyres.service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    // Injected so that loadUserByUsername always returns UserDetails with the
    // correct role from the DB — we never hardcode roles here.
    private final CustomUserDetailsService userDetailsService;

    @Override
    public AuthResponse signup(AuthRequest request) {
        // Throws DuplicateFoundException → GlobalExceptionHandler returns 409
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateFoundException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_USER");
        userRepository.save(newUser);

        // Load via service so UserDetails reflects actual DB state (role included)
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
        return new AuthResponse(jwtUtil.generateToken(userDetails), newUser.getId(), newUser.getRole());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // UsernameNotFoundException → GlobalExceptionHandler returns 401
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        // Same generic message for wrong password — prevents username enumeration
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        // Load real UserDetails (correct role from DB) before issuing token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(jwtUtil.generateToken(userDetails), user.getId(), user.getRole());
    }
}
