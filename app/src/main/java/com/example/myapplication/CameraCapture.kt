package com.example.myapplication

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraCapture : CameraActivity(), CvCameraViewListener2 {
    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var mViewModel: ScannerViewModel
    private lateinit var mConfirmButton: Button
    private var mSnapShot: Mat? = null
    private var mLoaderCallback: BaseLoaderCallback

    init {
        mLoaderCallback = object : BaseLoaderCallback(this) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    SUCCESS -> {
                        Log.d(Utils.TAG, "OpenCV loaded successfully")
                        mOpenCvCameraView.enableView()
                    }
                    else -> {
                        super.onManagerConnected(status)
                    }
                }
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                Log.d(Utils.TAG, "CameraCapture: no fragments in foreground")
                mConfirmButton.visibility = View.VISIBLE
            } else {
                Log.d(Utils.TAG, "CameraCapture: fragment is in foreground")
                mConfirmButton.visibility = View.GONE
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // ignore keyboard change
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_camera_capture)

        mConfirmButton = findViewById<View>(R.id.buttonOk) as Button
        mOpenCvCameraView = findViewById<View>(R.id.view) as CameraBridgeViewBase
        mOpenCvCameraView.setCvCameraViewListener(this)
        mViewModel = ViewModelProvider(this).get(ScannerViewModel::class.java)

        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mConfirmButton.visibility = View.VISIBLE
    }

    public override fun onPause() {
        super.onPause()
        mOpenCvCameraView.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                Utils.TAG,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            Log.d(Utils.TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.d(Utils.TAG, "onCameraViewStarted")
    }

    override fun onCameraViewStopped() {}
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        val frame = inputFrame.rgba()
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGBA)
        mSnapShot = frame

        return frame
    }

    override fun getCameraViewList(): List<CameraBridgeViewBase> {
        return listOf<CameraBridgeViewBase>(mOpenCvCameraView)
    }

    fun onButtonClicked(view: View?) {
        mViewModel.frame = mSnapShot
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView, PhotoFragment()
        ).addToBackStack(null).commit()
    }

    companion object {
        // for using local storage as photo supplier
        private fun getPath(file: String, context: Context): String {
            val assetManager = context.assets
            var inputStream: BufferedInputStream? = null
            try {
                // Read data from assets.
                inputStream = BufferedInputStream(assetManager.open(file))
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()
                // Create copy file in storage.
                val outFile = File(context.filesDir, file)
                val os = FileOutputStream(outFile)
                os.write(data)
                os.close()
                // Return a path to file which may be read in common way.
                return outFile.absolutePath
            } catch (ex: IOException) {
                Log.i(Utils.TAG, "Failed to upload a file")
            }
            return ""
        }
    }
}