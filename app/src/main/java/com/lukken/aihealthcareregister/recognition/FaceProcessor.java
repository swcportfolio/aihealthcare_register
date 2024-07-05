package com.lukken.aihealthcareregister.recognition;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import eu.id3.face.DetectedFace;
import eu.id3.face.DetectedFaceList;
import eu.id3.face.FaceCandidate;
import eu.id3.face.FaceCandidateList;
import eu.id3.face.FaceDetector;
import eu.id3.face.FaceEncoder;
import eu.id3.face.FaceException;
import eu.id3.face.FaceLibrary;
import eu.id3.face.FaceMatcher;
import eu.id3.face.FaceModel;
import eu.id3.face.FaceTemplate;
import eu.id3.face.FaceTemplateBufferType;
import eu.id3.face.FaceTemplateDict;
import eu.id3.face.ProcessingUnit;

public class FaceProcessor {
    private FaceDetector faceDetector = null;
    private FaceEncoder faceEncoder = null;

    String savePath;

    public FaceProcessor(Context context) {
        savePath = context.getExternalFilesDir("").getPath();
        String LOG_TAG = "FaceProcessor";
        try {
            /*
             * Load a face detector.
             * First load the model from the Assets and then initialize the FaceDetector object.
             * Only one FaceDetector object is needed to perform all of your detection operation.
             */
            FaceLibrary.loadModelBuffer(
                    readAllBytes(context.getAssets().open("models/face_detector_v3b.id3nn")),
                    FaceModel.FACE_DETECTOR_3B, ProcessingUnit.CPU
            );
            faceDetector = new FaceDetector();
            faceDetector.setConfidenceThreshold(id3Parameters.detectorConfidenceThreshold);
            faceDetector.setModel(FaceModel.FACE_DETECTOR_3B);
            faceDetector.setThreadCount(id3Parameters.detectorThreadCount);

            /*
             * Load a face encoder.
             * First load the model from the Assets and then initialize the FaceEncoder object.
             */
            FaceLibrary.loadModelBuffer(
                    readAllBytes(context.getAssets().open("models/face_encoder_v9b.id3nn")),
                    FaceModel.FACE_ENCODER_9B, ProcessingUnit.CPU
            );
            FaceLibrary.loadModelBuffer(
                    readAllBytes(context.getAssets().open("models/face_encoding_quality_estimator_v3a.id3nn")),
                    FaceModel.FACE_ENCODING_QUALITY_ESTIMATOR_3A, ProcessingUnit.CPU
            );
            faceEncoder = new FaceEncoder();
            faceEncoder.setModel(FaceModel.FACE_ENCODER_9B);
            faceEncoder.setThreadCount(id3Parameters.encoderThreadCount);
        } catch (FaceException | IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error while loading models: " + e.getMessage());
        }
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    /**
     * Copies all available data from in to out without closing any stream.
     */
    public static void copyAllBytes(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
        }
    }

    public DetectedFace detectLargestFace(eu.id3.face.Image image) {
        /* Track faces in the image. */
        DetectedFaceList detectedFaceList = faceDetector.detectFaces(image);
        if (detectedFaceList.getCount() > 0) {
            /* At least one face was detected! Return the largest one. */
            return detectedFaceList.getLargestFace();
        } else {
            /* No face was detected. */
            return null;
        }
    }

    public void saveTemplate(FaceTemplate template, String filename){
        //저장
        template.toFile(FaceTemplateBufferType.NORMAL, savePath+"/"+filename+".dat");
    }
    public void deleteTemplate(String filename){
        File f = new File(savePath+"/"+filename+".dat");
        if(f.exists())
            f.delete();
    }

    public FaceTemplate enrollLargestFace(eu.id3.face.Image image, DetectedFace detectedFace) {
        return faceEncoder.createTemplate(image, detectedFace);
    }

    /**
     *
     * @param image
     * @param detectedFace
     * @return
     */
    public int checkQuality(eu.id3.face.Image image, DetectedFace detectedFace){
        if(detectedFace == null)
            return -1;
        return faceEncoder.computeQuality(image, detectedFace);
    }
}
