package com.dolsk.tyres.service.service;


import com.dolsk.tyres.dto.*;

public interface AuthService {
    AuthResponse signup(AuthRequest request);
    AuthResponse login(AuthRequest request);
}