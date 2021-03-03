package com.facekey.recognition.service.impl;

import com.facekey.recognition.property.ApplicationConstants;
import com.facekey.recognition.service.RecognitionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class RecognitionServiceImpl implements RecognitionService {
    private static final Logger logger = LogManager.getLogger(RecognitionService.class);

    @Override
    public Integer recognizeImage(String fileSubPath) throws Exception {
        logger.info("Starting to recognize from " + this.toString());
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.setThreshold(ApplicationConstants.TRESHOLD);
        try {
            faceRecognizer.read(ApplicationConstants.FACEKEY_FILE_PATH + ApplicationConstants.FACEKEY_FILE_NAME);
        } catch (Exception e) {
            logger.error("Couldn't find base recognition file. Cannot recognize anything.");
            throw new Exception("Couldn't find base recognition file. Cannot recognize anything.");
        }

        String path = Paths.get(ApplicationConstants.FOLDER_PATH + "/" + fileSubPath).toAbsolutePath().toString();

        Mat testImage = imread(path, IMREAD_GRAYSCALE);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        try {
            faceRecognizer.predict(testImage, label, confidence);
            System.out.println(confidence.get() + "---" + (confidence.get() < 50));
        } catch (Exception e) {
            logger.error("Error while recognizing picture. " + e);
            throw new Exception("Error while recognizing picture. " + e);
        }
        return label.get(0);
    }

    @Override
    public void recognizeVideo(String fileSubPath) throws Exception {

        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();

        String filePath = Paths.get(ApplicationConstants.VIDEO_PATH + "/" + fileSubPath).toAbsolutePath().toString();

        try {
            faceRecognizer.read(ApplicationConstants.FACEKEY_FILE_PATH + ApplicationConstants.FACEKEY_FILE_NAME);
        } catch (Exception e) {
            logger.error("Couldn't find base recognition file. Cannot recognize anything.");
            throw new Exception("Couldn't find base recognition file. Cannot recognize anything.");
        }

        String cascadePath = Paths.get(ApplicationConstants.CASCADE_PATH + "/" + "haarcascade_frontalface_default.xml").toAbsolutePath().toString();

        CascadeClassifier face_cascade = new CascadeClassifier(cascadePath);

        File f = new File(filePath);

        OpenCVFrameGrabber grabber = null;
        try {
            grabber = OpenCVFrameGrabber.createDefault(f);
            grabber.start();
        } catch (Exception e) {
            System.err.println("Failed start the grabber.");
        }

        Frame videoFrame = null;
        Mat videoMat = new Mat();
        while (true) {
            videoFrame = grabber.grab();
            videoMat = converterToMat.convert(videoFrame);
            Mat videoMatGray = new Mat();
            // Convert the current frame to grayscale:
            cvtColor(videoMat, videoMatGray, COLOR_BGRA2GRAY);
            equalizeHist(videoMatGray, videoMatGray);

            Point p = new Point();
            RectVector faces = new RectVector();
            // Find the faces in the frame:
            face_cascade.detectMultiScale(videoMatGray, faces);

            // At this point you have the position of the faces in
            // faces. Now we'll get the faces, make a prediction and
            // annotate it in the video. Cool or what?
            for (int i = 0; i < faces.size(); i++) {
                Rect face_i = faces.get(i);

                Mat face = new Mat(videoMatGray, face_i);
                // If fisher face recognizer is used, the face need to be
                // resized.
                // resize(face, face_resized, new Size(im_width, im_height),
                // 1.0, 1.0, INTER_CUBIC);

                // Now perform the prediction, see how easy that is:
                IntPointer label = new IntPointer(1);
                DoublePointer confidence = new DoublePointer(1);
                faceRecognizer.predict(face, label, confidence);
                int prediction = label.get(0);

                // And finally write all we've found out to the original image!
                // First of all draw a green rectangle around the detected face:
                rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

                // Create the text we will annotate the box with:
                String name;
                switch (prediction){
                    case 1: name = "Emre";break;
                    case 2: name = "Ozenc";break;
                    case 3: name = "Ahmet";break;
                    default:name = "?";break;
                }

                String box_text = "Prediction = " + name;
                // Calculate the position for annotated text (make sure we don't
                // put illegal values in there):
                int pos_x = Math.max(face_i.tl().x() - 10, 0);
                int pos_y = Math.max(face_i.tl().y() - 10, 0);
                // And now put it into the image:
                putText(videoMat, box_text, new Point(pos_x, pos_y),
                        FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
            }
            // Show the result:
            imshow("face_recognizer", videoMat);

            char key = (char) waitKey(20);
            // Exit this loop on escape:
            if (key == 27) {
                destroyAllWindows();
                break;
            }
        }


    }
}
