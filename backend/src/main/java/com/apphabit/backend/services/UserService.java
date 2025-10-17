package com.apphabit.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.apphabit.backend.entities.User;
import com.apphabit.backend.models.UserRequest;
import com.apphabit.backend.models.UserResponse;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Long id);

    User save(User user);

    Optional<User> update(UserRequest user, Long id);

    void deleteById(Long id);
    
    Long getIdFromUsername(String username);
    
    List<UserResponse> findAllIdsExcept();
    
    UserResponse getUserFromUsername(String username);
}
