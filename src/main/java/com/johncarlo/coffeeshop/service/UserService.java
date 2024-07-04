package com.johncarlo.coffeeshop.service;

import com.johncarlo.coffeeshop.model.User;
import com.johncarlo.coffeeshop.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String signupUser(User user) {
        if(user.getName() == null || !user.getName().matches("^[A-Za-z0-9]{4,}$")) {
            return "Username must be at least 4 characters and contain only letters and numbers";
        }
        if(user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }
        if(user.getPassword() == null || user.getPassword().length() < 8) {
            return "Password must be at least 8 characters";
        }
        if(!user.getPassword().equals(user.getConfirmPassword())) {
            return "Passwords do not match";
        }
        if(userRepository.findOneByEmail(user.getEmail()) != null) {
            return "Username already exists";
        }

        userRepository.save(user);

        return null;
    }

    public User loginUser(User user) {
        return userRepository.findOneByEmailAndPassword(user.getEmail(), user.getPassword());
    }
}
