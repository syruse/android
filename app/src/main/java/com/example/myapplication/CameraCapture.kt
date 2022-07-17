package com.example.myapplication

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.CameraCaptureFragmentBinding
import com.example.myapplication.databinding.PhotoFragmentBinding
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class CameraCapture :  CameraFragment(), CvCameraViewListener2 {
    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var mViewModel: ScannerViewModel
    private lateinit var mConfirmButton: Button
    private lateinit var binding: CameraCaptureFragmentBinding
    private var mSnapShot: Mat? = null
    private var mLoaderCallback: BaseLoaderCallback

    init {
        mLoaderCallback = object : BaseLoaderCallback(context) {
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
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // ignore keyboard change
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CameraCaptureFragmentBinding.inflate(layoutInflater)
        mConfirmButton = binding.buttonOk
        mConfirmButton.setOnClickListener {
            mViewModel.setFrame( mSnapShot ?: Mat())
            findNavController().navigate(R.id.action_cameraCapture_to_photoFragment)
        }
        mOpenCvCameraView = binding.view
        mOpenCvCameraView.setCvCameraViewListener(this)
        mViewModel = ViewModelProvider(requireActivity()).get(ScannerViewModel::class.java)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        mOpenCvCameraView.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView.disableView()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                Utils.TAG,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            )
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, mLoaderCallback)
        } else {
            Log.d(Utils.TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
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
}