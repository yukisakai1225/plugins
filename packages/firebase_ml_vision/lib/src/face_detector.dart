// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

part of firebase_ml_vision;

class FaceDetector extends FirebaseVisionDetector {
  FaceDetector._(FaceDetectorOptions options);

  @override
  Future<void> close() async {
    return FirebaseVision.channel.invokeMethod('FaceDetector#close');
  }

  @override
  Future<List<Face>> detectInImage(FirebaseVisionImage visionImage) async {
    final List<dynamic> reply = await FirebaseVision.channel.invokeMethod(
      'FaceDetector#detectInImage',
      visionImage.imageFile.path,
    );

    return reply.map((dynamic face) => Face._(face)).toList(growable: false);
  }
}

class FaceDetectorOptions {}

class FaceBoundingBox {
  FaceBoundingBox._(Map<dynamic, dynamic> data)
      : boundingBox = data['left'] != null
            ? Rectangle<int>(
                data['left'],
                data['top'],
                data['width'],
                data['height'],
              )
            : null;

  /// Axis-aligned bounding rectangle of the detected face.
  ///
  /// Could be null even if face is found.
  final Rectangle<int> boundingBox;
}

/// A FirebaseVisionFace.
class Face {
  Face._(Map<dynamic, dynamic> face)
      : boundingBox = FaceBoundingBox._(face),
        smilingProbability = face['smilingProbability'],
        leftEyeOpenProbability = face['leftEyeOpenProbability'],
        rightEyeOpenProbability = face['rightEyeOpenProbability'];

  final FaceBoundingBox boundingBox;
  final double smilingProbability;
  final double leftEyeOpenProbability;
  final double rightEyeOpenProbability;
}
