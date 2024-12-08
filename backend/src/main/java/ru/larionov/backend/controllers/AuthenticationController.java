package ru.larionov.backend.controllers;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.larionov.backend.dto.auth.AuthRequest;
import ru.larionov.backend.dto.auth.AuthResponse;
import ru.larionov.backend.dto.auth.RefreshAuthRequest;
import ru.larionov.backend.security.JwtService;
import ru.larionov.backend.security.TypeToken;
import ru.larionov.backend.services.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    private AuthResponse authenticate(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                return new AuthResponse(
                        jwtService.generateToken(
                                userService.loadUserByUsername(authRequest.getUsername()),
                                TypeToken.ACCESS
                        ),
                        jwtService.generateToken(
                                userService.loadUserByUsername(authRequest.getUsername()),
                                TypeToken.REFRESH
                        )
                );
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (AuthenticationException | JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/refresh")
    private AuthResponse refreshAuthenticate(@RequestBody RefreshAuthRequest refreshAuthRequest) {
        try {
            if (Objects.nonNull(refreshAuthRequest.getRefreshToken()) && Strings.isNotBlank(refreshAuthRequest.getRefreshToken())) {
                String username = jwtService.extractUsername(refreshAuthRequest.getRefreshToken());
                return new AuthResponse(
                        jwtService.generateToken(
                                userService.loadUserByUsername(username),
                                TypeToken.ACCESS
                        ),
                        jwtService.generateToken(
                                userService.loadUserByUsername(username),
                                TypeToken.REFRESH
                        )
                );
            } else {
                throw new BadCredentialsException("Refresh token is not valid.");
            }
        } catch (AuthenticationException | JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
