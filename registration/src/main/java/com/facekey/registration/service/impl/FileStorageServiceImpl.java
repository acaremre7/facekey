package com.facekey.registration.service.impl;

import com.facekey.registration.model.entity.FaceEntity;
import com.facekey.registration.model.entity.UserEntity;
import com.facekey.registration.property.FileStorageProperties;
import com.facekey.registration.repository.FileStorageRepository;
import com.facekey.registration.service.FileStorageService;
import com.facekey.registration.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LogManager.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    @Autowired
    private FileStorageRepository fileStorageRepository;
    @Autowired
    private UserService userService;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) throws Exception {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            logger.fatal("Could not create the directory where the uploaded files will be stored." + e);
            throw new Exception("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String userName) throws Exception {
        Optional<UserEntity> userEntity = userService.getUserDetails(userName);
        if (!userEntity.isPresent()) {
            throw new Exception("User does not exist !");
        }
        int userId = userEntity.get().getUserId();
        String fileName = UUID.nameUUIDFromBytes(file.getBytes()).toString();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        fileStorageRepository.save(new FaceEntity(fileName, userId));
        return fileName;
    }
}
