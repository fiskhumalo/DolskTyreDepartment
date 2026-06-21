package com.dolsk.tyres.service.impl;

import com.dolsk.tyres.dto.AuthRequest;
import com.dolsk.tyres.dto.AuthResponse;
import com.dolsk.tyres.dto.ChangePasswordRequest;
import com.dolsk.tyres.exception.DuplicateFoundException;
import com.dolsk.tyres.model.User;
import com.dolsk.tyres.repository.UserRepository;
import com.dolsk.tyres.security.CustomUserDetailsService;
import com.dolsk.tyres.security.JwtUtil;
import com.dolsk.tyres.service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public AuthResponse signup(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateFoundException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_USER");
        userRepository.save(newUser);

        logger.info("[AUDIT] action=SIGNUP user={}", newUser.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
        return new AuthResponse(jwtUtil.generateToken(userDetails), newUser.getId(), newUser.getRole());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("[AUDIT] action=LOGIN_FAILED user={}", request.getUsername());
            throw new UsernameNotFoundException("Invalid username or password");
        }

        logger.info("[AUDIT] action=LOGIN user={} role={}", user.getUsername(), user.getRole());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(jwtUtil.generateToken(userDetails), user.getId(), user.getRole());
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.warn("[AUDIT] action=PASSWORD_CHANGE_FAILED user={}", username);
            throw new UsernameNotFoundException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        logger.info("[AUDIT] action=PASSWORD_CHANGED user={}", username);
    }
}
