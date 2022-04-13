package com.clone.instagram.service;

import com.clone.instagram.entity.User;
import com.clone.instagram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean save(User user) {
        if(userRepository.findByEmail(user.getEmail()) != null){
            return false;
        }

        System.out.println(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getPassword());
        System.out.println(user.getPhone());
        System.out.println(user.getTitle());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}
