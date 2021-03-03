package com.facekey.registration.service.impl;

import com.facekey.registration.model.entity.UserEntity;
import com.facekey.registration.repository.EmployeeStorageRepository;
import com.facekey.registration.service.UserService;
import com.facekey.registration.util.UserConverter;
import com.facekey.registration.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmployeeStorageRepository employeeStorageRepository;

    @Override
    public List<UserEntity> getAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        employeeStorageRepository.findAll().forEach(userEntityList::add);
        return userEntityList;
    }

    @Override
    public void createUser(UserEntity userEntity) throws SQLException {
        if (getUserDetails(userEntity.getUserName()).isPresent()) {
            throw new SQLException("User already exists !");
        } else {
            employeeStorageRepository.save(userEntity);
        }
    }

    @Override
    public Optional<UserEntity> getUserDetails(String userName) {
        return employeeStorageRepository.findById(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = employeeStorageRepository.findById(s);

        if (!userEntity.isPresent()) {
            throw new UsernameNotFoundException("User not found !");
        }
        try {
            UserValidator.validateUser(UserConverter.userEntityToUserDto(userEntity.get()));
        } catch (ValidationException e) {
            throw new UsernameNotFoundException("User not found !");
        }

        return new User(userEntity.get().getUserName(), userEntity.get().getPassword(), new ArrayList<>());
    }
}
