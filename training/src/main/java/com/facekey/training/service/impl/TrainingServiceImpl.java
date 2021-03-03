package com.facekey.training.service.impl;

import com.facekey.training.property.ApplicationConstants;
import com.facekey.training.service.TrainingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.opencv.core.CvType.CV_32SC1;


public class TrainingServiceImpl implements TrainingService {
    private static final Logger logger = LogManager.getLogger(TrainingService.class);


    @Override
    public void train(String fileSubPath, int label) {
        logger.info("Starting to train from " + this.toString());
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        //faceRecognizer.setThreshold(ApplicationConstants.TRESHOLD);
        boolean created = false;
        try {
            faceRecognizer.read(ApplicationConstants.FACEKEY_FILE_PATH + ApplicationConstants.FACEKEY_FILE_NAME);
        } catch (Exception e) {
            created = true;
            logger.info("Couldn't find base recognition file. Will create a new one. :" + e);
        }

        Path filePath = Paths.get(ApplicationConstants.FOLDER_PATH + "/" + fileSubPath);
        String path = filePath.toAbsolutePath().toString();

        Mat img = imread(path, IMREAD_GRAYSCALE);

        MatVector images = new MatVector(1);
        Mat labels = new Mat(1, 1, CV_32SC1);

        IntBuffer labelsBuf = labels.createBuffer();

        images.put(0L, img);
        labelsBuf.put(0, label);

        if(created){
            faceRecognizer.train(images, labels);
        }else {
            faceRecognizer.update(images, labels);
        }

        try {
            faceRecognizer.write(ApplicationConstants.FACEKEY_FILE_PATH + ApplicationConstants.FACEKEY_FILE_NAME);
            //System.out.println("----TRESHOLD---> " + faceRecognizer.getThreshold());
            logger.info("Face training successful.");
        } catch (Exception e) {
            logger.error("OpenCV Error while saving face recognition file: " + e);
            throw e;
        }
    }
}
