package com.API.FlutterAPI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.API.FlutterAPI.dto.RegisterRequest;
import com.API.FlutterAPI.model.User;
import com.API.FlutterAPI.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(registerRequest.getEmail());
        Optional<User> existingUserByMobile = userRepository.findByMobile(registerRequest.getMobile());

        if (existingUserByEmail.isPresent() || existingUserByMobile.isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User(
            registerRequest.getName(),
            registerRequest.getEmail(),
            registerRequest.getMobile(),
            passwordEncoder.encode(registerRequest.getPassword())
        );
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}