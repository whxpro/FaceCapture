package com.facecapture.prod.facedetect;

import com.google.mlkit.vision.face.Face;

public interface OnFaceDetectListener {
    void onFaceDetect(Face face);
}