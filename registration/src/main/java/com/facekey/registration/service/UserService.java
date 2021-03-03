package com.facekey.registration.service;

import com.facekey.registration.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<UserEntity> getAll();

    void createUser(UserEntity userEntity) throws SQLException;

    Optional<UserEntity> getUserDetails(String userName);
}
