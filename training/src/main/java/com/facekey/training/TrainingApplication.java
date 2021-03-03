package com.facekey.training;

import com.facekey.training.model.FaceEntity;
import com.facekey.training.property.ApplicationConstants;
import com.facekey.training.repository.FileStorageRepository;
import com.facekey.training.service.TrainingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class TrainingApplication {
    private static final Logger logger = LogManager.getLogger(TrainingApplication.class);

    private static FileStorageRepository fileStorageRepository;

    @Autowired
    public TrainingApplication(FileStorageRepository fileStorageRepository){
        TrainingApplication.fileStorageRepository = fileStorageRepository;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TrainingApplication.class, args);
        listen();
    }

    private static void listen() throws Exception {
        try {
            Files.createDirectories(Paths.get(ApplicationConstants.FOLDER_PATH));
        } catch (Exception e) {
            logger.fatal("Could not create the directory where the uploaded files will be stored: " + e);
            throw new Exception("Could not create the directory where the uploaded files will be stored.", e);
        }
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        TrainingService trainingService = (TrainingService) context.getBean("TrainingService");

        //Pull data from DB, and process if there's any entry without isProcessed flag set
        while (true) {
            try {
                Thread.sleep(10000);
                for (FaceEntity faceEntity : fileStorageRepository.findByIsProcessed(false)) {
                    trainingService.train(faceEntity.getFileName(), faceEntity.getUserId());
                    faceEntity.setIsProcessed(true);
                    fileStorageRepository.save(faceEntity);
                }
            } catch (Exception e) {
                logger.error("Exception while processing " + "filename" + ": " + e.getMessage());
            }
        }
    }
}
