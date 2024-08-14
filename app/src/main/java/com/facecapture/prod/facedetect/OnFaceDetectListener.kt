package com.facecapture.prod.facedetect

import com.google.mlkit.vision.face.Face

interface OnFaceDetectListener {
    fun onFaceDetect(face: Face?)
}