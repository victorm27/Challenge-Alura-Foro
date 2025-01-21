package com.foro.foro_hub.controllers;

import com.foro.foro_hub.dto.AuthenticationRequest;
import com.foro.foro_hub.dto.AuthenticationResponse;
import com.foro.foro_hub.dto.RegisterUserRequest;
import com.foro.foro_hub.models.User;
import com.foro.foro_hub.security.JwtService;
import com.foro.foro_hub.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        User user = userService.registerUser(
                request.username(),
                request.email(),
                request.password()
        );

        String token = jwtService.generateToken(
                userService.loadUserByUsername(user.getUsername())
        );

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = userService.loadUserByUsername(request.username());
        var token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}