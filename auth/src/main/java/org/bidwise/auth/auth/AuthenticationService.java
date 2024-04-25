package org.bidwise.auth.auth;

import lombok.RequiredArgsConstructor;
import org.bidwise.auth.entity.UserEntity;
import org.bidwise.auth.repository.UserRepository;
import org.bidwise.auth.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        UserEntity user = UserEntity.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getUserRole())
                .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateJwtToken(user);

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        UserEntity user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(Exception::new);

        String jwtToken = jwtService.generateJwtToken(user);

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }
}
