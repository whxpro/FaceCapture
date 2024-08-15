package com.facecapture.prod

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facecapture.prod.databinding.ActivityCaptureBinding
import com.facecapture.prod.databinding.ActivityMainBinding
import com.facecapture.prod.facedetect.CameraSource
import com.facecapture.prod.facedetect.FaceDetectorProcessor
import com.facecapture.prod.facedetect.OnFaceDetectListener
import com.facecapture.prod.live2d.GLRenderer
import com.facecapture.prod.live2d.LAppDelegate
import com.facecapture.prod.live2d.LAppLive2DManager
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import com.live2d.sdk.cubism.framework.CubismDefaultParameterId
import com.live2d.sdk.cubism.framework.CubismFramework
import java.io.IOException
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class CaptureActivity  : AppCompatActivity(), OnFaceDetectListener {
    companion object {
        private const val TAG = "CaptureFaceActivity"
    }

    private val mBinding by lazy { ActivityCaptureBinding.inflate(layoutInflater) }

    private var cameraSource: CameraSource? = null

    private lateinit var glRenderer: GLRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setFullscreen()

        setupViews()
    }

    override fun onStart() {
        super.onStart()

        LAppDelegate.getInstance().onStart(this)
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()

        mBinding.live2dView.onResume()

        val decor = this.window.decorView
        decor.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        createCameraSource()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()

        mBinding.live2dView.onPause()
        LAppDelegate.getInstance().onPause()

        mBinding.cameraPreview.stop()
    }

    override fun onStop() {
        super.onStop()
        LAppDelegate.getInstance().onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        LAppDelegate.getInstance().onDestroy()

        cameraSource?.release()
    }

    private fun createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, mBinding.overlay)
        }
        cameraSource!!.setMachineLearningFrameProcessor(
            FaceDetectorProcessor(this, this)
        )
        cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                mBinding.cameraPreview.start(cameraSource, mBinding.overlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }


    override fun onFaceDetect(face: Face?) {
        face ?: return
//        solveFacePose(face.getLandmark(FaceLandmark.LEFT_EYE)?.position,
//            face.getLandmark(FaceLandmark.RIGHT_EYE)?.position,
//            face.getLandmark(FaceLandmark.NOSE_BASE)?.position,
//            face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position,
//            face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
//        )

        openMouth(face)
        winkEye(face)

        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

        val angleY = face.headEulerAngleY
        LAppDelegate.angleX = -angleY

        val angleZ = face.headEulerAngleX
        LAppDelegate.angleY = angleZ


//        Log.d(TAG, "onDrag: x: $x, y: $y")

//        LAppLive2DManager.getInstance().onDrag(x, y)
    }

    private val r_m = 0.4
    private val r_n = 0.5

    private fun solveFacePose(leftEye: PointF?, rightEye: PointF?, noseTip: PointF?, mouthLeft: PointF?, mouthRight: PointF?) {
        if (leftEye == null || rightEye == null || noseTip == null || mouthLeft == null || mouthRight == null) {
            Log.d(TAG, "solveFacePose, some point is null")
            return
        }

        val noseBase = PointF((leftEye.x + rightEye.x) / 2, (leftEye.y + rightEye.y) / 2)
        val mouth = PointF((mouthLeft.x + mouthRight.x) / 2, (mouthLeft.y + mouthRight.y) / 2)

        val n = Point(
            (mouth.x + (noseBase.x - mouth.x) * r_m).toInt(),
            (mouth.y + (noseBase.y - mouth.y) * r_m).toInt()
        )

        val theta = acos(
            ((noseBase.x - n.x) * (noseTip.x - n.x) + (noseBase.y - n.y) * (noseTip.y - n.y)).toDouble() / hypot(
                (noseTip.x - n.x).toDouble(),
                (noseTip.y - n.y).toDouble()
            ) / hypot((noseBase.x - n.x).toDouble(), (noseBase.y - n.y).toDouble())
        )
        val tau = atan2((n.y - noseTip.y).toDouble(), (n.x - noseTip.x).toDouble())

        val m1 = ((noseTip.x - n.x) * (noseTip.x - n.x) + (noseTip.y - n.y) * (noseTip.y - n.y)).toDouble() /
                ((noseBase.x - mouth.x) * (noseBase.x - mouth.x) + (noseBase.y - mouth.y) * (noseBase.y - mouth.y))
        val m2 = cos(theta) * cos(theta)
        val a: Double = r_n * r_n * (1 - m2)
        val b: Double = m1 - r_n * r_n + 2 * m2 * r_n * r_n
        val c: Double = -m2 * r_n * r_n

        val delta = acos(sqrt((sqrt(b * b - 4 * a * c) - b) / (2 * a)))

        //fn: facial normal, sfn: standard(no rotation) facial normal
        val fn = DoubleArray(3)
        val sfn = DoubleArray(3)
        fn[0] = sin(delta) * cos(tau)
        fn[1] = sin(delta) * sin(tau)
        fn[2] = -cos(delta)

        val alpha = Math.PI / 12
        sfn[0] = 0.0
        sfn[1] = sin(alpha)
        sfn[2] = -cos(alpha)

        //PITCH:X YAW:Y ROLL:X
        //Log.d(TAG, "facial normal: " + fn[0] + " " + fn[1] + " " + fn[2]);
        //Log.d(TAG, "standard facial normal: " + sfn[0] + " " + sfn[1] + " " + sfn[2]);

        /*
        live2d rotation order: ZXY
        live2d coordinate           my coordinate           my coordinate
        angle Z                     z axis                  Yaw
        angle X                     y axis                  Pitch
        angle Y                     x axis                  Roll

        my coordinate is same as the paper:
        Estimating Gaze from a Single View of a Face
        link: ieeexplore.ieee.org/document/576433/
         */

        // (w, x, y, z) is Euler quaternion
        //
        val w: Double
        val angle = acos(
            (sfn[0] * fn[0] + sfn[1] * fn[1] + sfn[2] * fn[2]) / sqrt(
                sfn[0] * sfn[0] + sfn[1] * sfn[1] + sfn[2] * sfn[2]
            ) / sqrt(fn[0] * fn[0] + fn[1] * fn[1] + fn[2] * fn[2])
        )
        w = cos(0.5 * angle)
        var x = sfn[1] * fn[2] - sfn[2] * fn[1]
        var y = sfn[2] * fn[0] - sfn[0] * fn[2]
        var z = sfn[0] * fn[1] - sfn[1] * fn[0]

        val l = sqrt(x * x + y * y + z * z)
        x = sin(0.5 * angle) * x / l
        y = sin(0.5 * angle) * y / l
        z = sin(0.5 * angle) * z / l
        var roll: Double
        roll = atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y))
        var pitch = asin(2 * (w * y - z * x))

        //Log.d(TAG, "Angle: " + w);
        var yaw = atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z))

        //        if(yaw < Math.PI / 18) {
        if (sfn[0] < 0.1 && sfn[1] < 0.1) {
            roll = 1.5 * atan2((rightEye.y - leftEye.y).toDouble(), (rightEye.x - leftEye.x).toDouble())
        }
        yaw = max(-30.0, min(30.0, yaw * 180 / Math.PI))
        pitch = max(-30.0, min(30.0, pitch * 180 / Math.PI))
        roll = max(-30.0, min(30.0, roll * 180 / Math.PI))

        Log.d(TAG, "Yaw: $yaw Pitch: $pitch Roll: $roll")

        LAppDelegate.angleX = -(String.format("%.1f", pitch, Locale.US).toFloat())
        LAppDelegate.angleY = String.format("%.1f", roll, Locale.US).toFloat()

    }

    private fun openMouth(face: Face) {
        val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position
        val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
        val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position

        Log.d(TAG, "openMouth, bottom: $mouthBottom, left: $mouthLeft, right: $mouthRight")

        if (mouthBottom == null || mouthLeft == null || mouthRight == null) {
            return
        }

//        val a = sqrt((mouthBottom.x - mouthLeft.x) * (mouthBottom.x - mouthLeft.x) + (mouthBottom.y - mouthLeft.y) * (mouthBottom.y - mouthLeft.y))
//        val b = sqrt((mouthBottom.x - mouthRight.x) * (mouthBottom.x - mouthRight.x) + (mouthBottom.y - mouthRight.y) * (mouthBottom.y - mouthRight.y))
//        val c = sqrt((mouthLeft.x - mouthRight.x) * (mouthLeft.x - mouthRight.x) + (mouthLeft.y - mouthRight.y) * (mouthLeft.y - mouthRight.y))
//
//        val degreeC = acos((a * a + b * b - c * c) / (2 * a * b))
//        val d = degreeC / Math.PI * 180

//        Log.d(TAG, "a: $a, b: $b, c: $c, degreeC: $degreeC, d: $d")

        val width = mouthRight.x - mouthLeft.x
        val height = mouthBottom.y - (mouthLeft.y + mouthRight.y) / 2

        val threshold = width * 0.23

        Log.d(TAG, "width: $width, height: $height, open: ${height > threshold}")

        LAppDelegate.mouthY = if (height > threshold) 1f else 0f
    }

    private fun winkEye(face: Face) {
        LAppDelegate.leftEyeOpenProbability = face.leftEyeOpenProbability ?: return
        LAppDelegate.rightEyeOpenProbability = face.rightEyeOpenProbability ?: return
    }

    @Suppress("DEPRECATION")
    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            window.insetsController?.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())

            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupViews() {
        mBinding.live2dView.setEGLContextClientVersion(2) // OpenGL ES 2.0を利用

        glRenderer = GLRenderer()
        mBinding.live2dView.setRenderer(glRenderer)
        mBinding.live2dView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        mBinding.changeModel.setOnClickListener {
            LAppDelegate.getInstance().view.changeModel()
        }
    }
}