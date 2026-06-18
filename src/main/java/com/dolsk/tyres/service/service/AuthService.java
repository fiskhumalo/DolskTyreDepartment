package com.dolsk.tyres.service.service;

import com.dolsk.tyres.dto.AuthRequest;
import com.dolsk.tyres.dto.AuthResponse;
import com.dolsk.tyres.dto.ChangePasswordRequest;

public interface AuthService {

    AuthResponse signup(AuthRequest request);

    AuthResponse login(AuthRequest request);

    /**
     * Changes the password for the authenticated user.
     *
     * @param username   resolved from JWT (never from user input)
     * @param request    contains currentPassword and newPassword
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if user not found
     * @throws com.dolsk.tyres.exception.ResourceNotFoundException if current password is wrong
     */
    void changePassword(String username, ChangePasswordRequest request);
}
