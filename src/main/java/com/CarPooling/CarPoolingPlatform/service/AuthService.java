package com.CarPooling.CarPoolingPlatform.service;


import com.CarPooling.CarPoolingPlatform.dto.LoginRequest;
import com.CarPooling.CarPoolingPlatform.dto.RegisterRequest;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import com.CarPooling.CarPoolingPlatform.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        String role = request.getRole();

        if (!role.equals("PASSENGER") && !role.equals("DRIVER")) {
            throw new RuntimeException("Invalid role");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .Phone(request.getPhone())
<<<<<<< HEAD
                .role(role)
=======
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }


        return jwtUtil.generateToken(user.getEmail(),user.getRole());
    }
}