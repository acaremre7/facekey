package com.facekey.recognition;

import com.facekey.recognition.property.ApplicationConstants;
import com.facekey.recognition.service.RecognitionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class RecognitionApplication {
	private static final Logger logger = LogManager.getLogger(RecognitionApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RecognitionApplication.class, args);
		recognize();
	}

	private static void recognize() throws Exception {
		try {
			Files.createDirectories(Paths.get(ApplicationConstants.FOLDER_PATH));
		} catch (Exception e) {
			logger.fatal("Could not create the directory where the uploaded files will be stored: " + e);
			throw new Exception("Could not create the directory where the uploaded files will be stored.", e);
		}
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
		RecognitionService recognitionService = (RecognitionService) context.getBean("RecognitionService");
		try {
//			System.out.println("Person: " + recognitionService.recognizeImage("e6.jpg"));
			//recognitionService.recognizeVideo("1.mp4");
			recognitionService.recognizeVideo("2.mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
