package com.pack.service;

import com.pack.common.enums.Role;
import com.pack.entity.User;
import com.pack.repository.UserRepo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    User getById(Long id);

    User validateAndAddUser( String phone);

    User getByPhoneNumber(String phoneNumber);

    void deleteUserById(Long id);

    void lockTheUserById(Long id);

    void unlockTheUserById(Long id);

    void lockTheUserByPhoneNumber(String phoneNumber);

    void unlockTheUserByPhoneNumber(String phoneNumber);

    List<User> getAllUser();

    List<User> getAllUsersPaginated(Integer page, Integer size);
}
