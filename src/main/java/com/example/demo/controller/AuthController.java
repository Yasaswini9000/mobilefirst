package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

 @RestController @RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register( @Valid @RequestBody AuthRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole("USER");
        userRepository.save(u);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login( @Valid @RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            String token = jwtUtil.generateToken(req.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}