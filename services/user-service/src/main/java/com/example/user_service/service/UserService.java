package com.example.user_service.service;

import com.example.common.events.component.JwtUtil;
import com.example.user_service.DTOs.UserLoginRequest;
import com.example.user_service.DTOs.UserRegisterRequest;
import com.example.user_service.DTOs.UserResponse;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserResponse register(UserRegisterRequest request){
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .role(saved.getRole())
                .build();

    }

    public String login(UserLoginRequest request){
        User user= userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        return jwtUtil.generateToken(user.getUsername());

    }
}
