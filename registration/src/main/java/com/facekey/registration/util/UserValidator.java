package com.facekey.registration.util;

import com.facekey.registration.model.CredentialsDto;
import com.facekey.registration.model.UserDto;

import javax.xml.bind.ValidationException;

public class UserValidator {

    private UserValidator() {
    }

    public static void validateUser(UserDto userModel) throws ValidationException {
        if (userModel == null) {
            throw new ValidationException("User object cannot be null !");
        } else if (userModel.getFirstName().isEmpty()) {
            throw new ValidationException("First name cannot be null !");
        } else if (userModel.getLastName().isEmpty()) {
            throw new ValidationException("Last name cannot be null !");
        }
        validateCredentials(userModel);
    }

    public static void validateCredentials(CredentialsDto credentialsDto) throws ValidationException {
        if (credentialsDto == null) {
            throw new ValidationException("Authentication object cannot be null !");
        } else if (credentialsDto.getUserName().isEmpty()) {
            throw new ValidationException("Username cannot be null !");
        } else if (credentialsDto.getPassword().isEmpty()) {
            throw new ValidationException("Password cannot be null !");
        }
    }
}
