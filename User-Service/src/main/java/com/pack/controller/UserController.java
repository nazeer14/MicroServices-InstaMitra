package com.pack.controller;

import com.pack.entity.User;
import com.pack.service.UserService;
import lombok.RequiredArgsConstructor;
import com.pack.common.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public String adminAccess() {
        return "Welcome Admin!";
    }


    @GetExchange("/get-user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id){
        User user=userService.getById(id);
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setPhone(user.getPhoneNumber());
        userDto.setVerified(user.isVerified());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<?> getUserByNumber(@PathVariable("phoneNumber") String phoneNumber){
        User user=userService.getByPhoneNumber(phoneNumber);
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setPhone(user.getPhoneNumber());
        userDto.setVerified(user.isVerified());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted");
    }

}
