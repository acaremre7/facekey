package com.facekey.registration.util;

import com.facekey.registration.model.UserDto;
import com.facekey.registration.model.entity.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserConverter {

    private UserConverter(){}

    public static UserEntity userDtoToUserEntity(UserDto userDto) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return new UserEntity(userDto.getUserName(), bCryptPasswordEncoder.encode(userDto.getPassword()), userDto.getFirstName(), userDto.getLastName(), userDto.getAdId(), userDto.getUserId());
    }

    public static UserDto userEntityToUserDto(UserEntity userEntity) {
        return new UserDto(userEntity.getUserName(), userEntity.getPassword(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getAdId(), userEntity.getUserId());
    }
}
