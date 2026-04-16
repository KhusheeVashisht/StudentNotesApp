package com.studentnotesapp.service;

import com.studentnotesapp.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createToken(String username) {
        return jwtUtil.generateToken(username);
    }
}
