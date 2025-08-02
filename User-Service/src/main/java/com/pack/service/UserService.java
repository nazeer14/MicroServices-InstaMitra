package com.pack.service;

import com.pack.entity.User;
import com.pack.repository.UserRepo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User getByPhoneNumber(String phoneNumber){
        Optional<User> user=userRepo.findByPhoneNumber(phoneNumber);
        if(user.isEmpty()){
            throw new IllegalArgumentException("User not found with Number: "+phoneNumber);
        }
        return user.get();
    }

    public User createUser(User newUser){
        Optional<User> user1=userRepo.findByPhoneNumber(newUser.getPhoneNumber());
        if(user1.isPresent())
        {
            throw new IllegalArgumentException("Number already in Use");
        }
        User user=new User();
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.setVerified(true);
        return userRepo.save(user);
    }

    public User getById(Long id){
       Optional<User> user= userRepo.findById(id);
       if(user.isEmpty()){
           throw new IllegalArgumentException("User not found with Id: "+id);
       }
        return user.get();
    }

    public List<User> getAllUser(){
        return userRepo.findAll();
    }

    public void deleteUserById(Long id) {
        Optional<User> user=userRepo.findById(id);
        if(user.isEmpty()){
            throw new IllegalArgumentException("Invalid User");
        }
        User user1=user.get();
        user1.setDeleted(true);
        userRepo.save(user1);
    }

    public User validateAndAddUser(@NotNull(message = "Number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phone) {
        Optional<User> user= userRepo.findByPhoneNumber(phone);
        if(user.isPresent())
        {
            User provider1=user.get();
            provider1.setVerified(true);
            provider1.setLogged(true);
            return userRepo.save(provider1);
        }
        User provider1=new User();
        provider1.setPhoneNumber(phone);
        provider1.setVerified(true);
        provider1.setLogged(true);
        return userRepo.save(provider1);
    }
}
