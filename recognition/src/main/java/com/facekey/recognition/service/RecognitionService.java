package com.facekey.recognition.service;

public interface RecognitionService {
    Integer recognizeImage(String fileSubPath) throws Exception;

    void recognizeVideo(String fileSubPath) throws Exception;
}
