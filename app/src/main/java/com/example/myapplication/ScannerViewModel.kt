package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import org.opencv.core.Scalar
import androidx.lifecycle.MutableLiveData
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint2f
import org.opencv.imgproc.Imgproc

class ScannerViewModel : ViewModel() {
    private val CONTOUR_COLOR = Scalar(0.0, 255.0, 0.0, 255.0)
    private val mFrame = MutableLiveData<Mat>()

    var frame: Mat?
        get() = mFrame.value
        set(frame) = mFrame.postValue(frame)

    fun makeScanning(frame: Mat, isDecoratedSrcByContour: Boolean): Mat? {
        val outFrame = frame.clone()
        val frameSize = frame.size()
        Log.d(Utils.TAG, " frameSize: $frameSize")
        val contours = Utils.findContours(frame)
        val screenRect = Mat(4, 1, CvType.CV_32FC2)
        screenRect.put(0, 0, frameSize.width, 0.0)
        screenRect.put(1, 0, 0.0, 0.0)
        screenRect.put(2, 0, 0.0, frameSize.height)
        screenRect.put(3, 0, frameSize.width, frameSize.height)
        val approximatedContour = MatOfPoint2f()

        // is this quad
        if (Utils.recognizeOuterQuad(contours, approximatedContour) >= 0) {
            if (isDecoratedSrcByContour) {
                Imgproc.drawContours(
                    frame,
                    listOf(MatOfPoint(*approximatedContour.toArray())),
                    0, CONTOUR_COLOR, 8, Imgproc.LINE_AA
                )
            }
            Log.d(
                Utils.TAG,
                " approximatedContour " + approximatedContour.rows() + " " + approximatedContour.cols()
            )
            val transformation = Imgproc.getPerspectiveTransform(approximatedContour, screenRect)
            Log.d(Utils.TAG, " approximated contour " + approximatedContour.dump())
            Log.d(Utils.TAG, " 1 " + approximatedContour[0, 0][0])
            Log.d(Utils.TAG, " 2 " + approximatedContour[1, 0][0])
            Log.d(Utils.TAG, " 3 " + approximatedContour[2, 0][0])
            Log.d(Utils.TAG, " 4 " + approximatedContour[3, 0][0])
            Imgproc.warpPerspective(frame, outFrame, transformation, frameSize)

            return outFrame
        } else {
            return null;
        }
    }
}