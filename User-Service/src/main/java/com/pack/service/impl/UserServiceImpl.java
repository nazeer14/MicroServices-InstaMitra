package com.pack.service.impl;

import com.pack.common.enums.Role;
import com.pack.entity.User;
import com.pack.repository.UserRepo;
import com.pack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public User getByPhoneNumber(String phoneNumber) {
        return userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with number: " + phoneNumber));
    }

    public User getById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with ID: " + id));
    }

    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public List<User> getAllUsersPaginated(Integer page, Integer size) {
        if (page == null || page < 0) page = 0;
        if (size == null || size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepo.findAll(pageable);
        return userPage.getContent();
    }

    public void deleteUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with ID: " + id));

        user.setLocked(true);
        user.setDeleted(true);
        userRepo.save(user);
    }

    public User validateAndAddUser(String phone) {
        Optional<User> userOpt = userRepo.findByPhoneNumber(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isDeleted()) {
                user.setDeleted(false);
            }
            user.setVerified(true);
            user.setLogged(true);
            return userRepo.save(user);
        }
        User newUser = new User();
        newUser.setPhoneNumber(phone);
        newUser.setVerified(true);
        newUser.setLogged(true);
        newUser.setRole(Role.USER);
        return userRepo.save(newUser);
    }

    @Override
    public void lockTheUserById(Long id) {
        changeLockStatusById(id, true);
    }

    @Override
    public void unlockTheUserById(Long id) {
        changeLockStatusById(id, false);
    }

    @Override
    public void lockTheUserByPhoneNumber(String phoneNumber) {
        changeLockStatusByPhone(phoneNumber, true);
    }

    @Override
    public void unlockTheUserByPhoneNumber(String phoneNumber) {
        changeLockStatusByPhone(phoneNumber, false);
    }

    // ======================= PRIVATE HELPERS =======================

    private void changeLockStatusById(Long id, boolean lock) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with ID: " + id));

        validateLockStatus(user, lock);
        user.setLocked(lock);
        userRepo.save(user);
    }

    private void changeLockStatusByPhone(String phoneNumber, boolean lock) {
        User user = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with phone: " + phoneNumber));

        validateLockStatus(user, lock);
        user.setLocked(lock);
        userRepo.save(user);
    }

    private void validateLockStatus(User user, boolean lock) {
        if (lock && user.isLocked()) {
            throw new ResponseStatusException(BAD_REQUEST, "User already locked");
        }
        if (!lock && !user.isLocked()) {
            throw new ResponseStatusException(BAD_REQUEST, "User already unlocked");
        }
    }
}
