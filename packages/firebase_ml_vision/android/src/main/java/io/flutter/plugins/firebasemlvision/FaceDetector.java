package io.flutter.plugins.firebasemlvision;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

class FaceDetector implements Detector {
  public static final FaceDetector instance = new FaceDetector();
  private static FirebaseVisionFaceDetector faceDetector;

  private FaceDetector() {}


  @Override
  public void handleDetection(FirebaseVisionImage image, final MethodChannel.Result result) {
    if (faceDetector == null) faceDetector = FirebaseVision.getInstance().getVisionFaceDetector();
    faceDetector
            .detectInImage(image)
            .addOnSuccessListener(
                    new OnSuccessListener<List<FirebaseVisionFace>>() {
                      @Override
                      public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        List<Map<String, Object>> faces = new ArrayList<>();
                        for (FirebaseVisionFace face : firebaseVisionFaces) {
                          faces.add(getFaceData(face));
                        }
                        result.success(faces);
                      }
                    })
            .addOnFailureListener(
                    new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception exception) {
                        result.error("faceDetectorError", exception.getLocalizedMessage(), null);
                      }
                    });
  }

  @Override
  public void close(MethodChannel.Result result) {
    if (faceDetector != null) {
      try {
        faceDetector.close();
        result.success(null);
      } catch (IOException exception) {
        result.error("faceDetectorError", exception.getLocalizedMessage(), null);
      }

      faceDetector = null;
    }
  }

  private Map<String, Object> getFaceData(FirebaseVisionFace face) {
    Map<String, Object> faceData = new HashMap<>();

    Rect boundingBox = face.getBoundingBox();
    if (boundingBox != null) {
      faceData.put("left", boundingBox.left);
      faceData.put("top", boundingBox.top);
      faceData.put("width", boundingBox.width());
      faceData.put("height", boundingBox.height());
    }

    faceData.put("smilingProbability", face.getSmilingProbability());
    faceData.put("leftEyeOpenProbability", face.getLeftEyeOpenProbability());
    faceData.put("rightEyeOpenProbability", face.getRightEyeOpenProbability());

    return faceData;
  }
}
