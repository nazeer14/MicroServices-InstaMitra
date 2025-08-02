package com.pack.repository;


import com.pack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
}
