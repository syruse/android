package com.example.myapplication

import android.content.Context
import android.util.Log
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.util.ArrayList

object Utils {
    const val TAG = "Scanner"
    private val CONTOUR_COLOR = Scalar(0.0, 255.0, 0.0, 255.0)

    fun convertRectToMatOfPoint(rect: Rect): MatOfPoint2f {
        val mPoints = MatOfPoint2f()
        val points: MutableList<Point> = ArrayList()
        points.add(Point((rect.x + rect.width).toDouble(), rect.y.toDouble()))
        points.add(Point(rect.x.toDouble(), rect.y.toDouble()))
        points.add(Point(rect.x.toDouble(), (rect.y + rect.height).toDouble()))
        mPoints.fromList(points)
        return mPoints
    }

    fun findContours(inputFrame: Mat): List<MatOfPoint> {
        val frame = inputFrame.clone()
        val blurred = Mat.zeros(frame.size(), frame.type())
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY)

        // calibrated values used generating the best result
        Imgproc.bilateralFilter(frame, blurred, 9, 25.0, 25.0)
        Imgproc.Canny(blurred, frame, 30.0, 200.0)
        val mHierarchy = Mat()
        val contours: List<MatOfPoint> = ArrayList()
        Imgproc.findContours(
            frame, contours, mHierarchy, Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        return contours
    }

      /**
      * @return -1 if not found or index of detected rect
      * @param contours is input param with all candidates
      * @param out_approximatedContour is output contour with edge points (4 points for rect)
      */
    fun recognizeOuterQuad(contours: List<MatOfPoint>, out_approximatedContour: MatOfPoint2f): Int {
        val largestArea = object {
            var area = 0.0
            var index = -1
            var approximatedContour: MatOfPoint2f? = null
        }

        contours.forEachIndexed { index, contour: MatOfPoint ->
            val area = Imgproc.contourArea(contour)
            val perimeter = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
            val approximatedContour = MatOfPoint2f()
            Imgproc.approxPolyDP(
                MatOfPoint2f(*contour.toArray()),
                approximatedContour,
                0.02 * perimeter,
                true
            )
            Log.d(TAG, " area " + area + " " + approximatedContour.rows())
            /// aside from having larger area it must be rectangular
            if (largestArea.area < area && approximatedContour.rows() == 4) {
                largestArea.area = area
                largestArea.index = index
                largestArea.approximatedContour = approximatedContour
                Log.d(TAG, " potential largest area $index $area")
            }
        }
        // fromArray is called to modify input data
        if(largestArea.index >= 0){
            out_approximatedContour.fromArray(*largestArea.approximatedContour?.toArray())
        }

        Log.d(TAG, " largestArea " + largestArea.index + " " + contours.size)
        //final var contour = contours.get(largestArea.index);
        //Imgproc.drawContours(inputFrame.rgba(), contours, largestArea.index, CONTOUR_COLOR, 8, Imgproc.LINE_AA);
        return largestArea.index
    }

    fun makeScanning(frame: Mat, isDecoratedSrcByContour: Boolean): Mat? {
        val outFrame = frame.clone()
        val frameSize = frame.size()
        Log.d(TAG, " frameSize: $frameSize")
        val contours = Utils.findContours(frame)
        val screenRect = Mat(4, 1, CvType.CV_32FC2)
        screenRect.put(0, 0, frameSize.width, 0.0)
        screenRect.put(1, 0, 0.0, 0.0)
        screenRect.put(2, 0, 0.0, frameSize.height)
        screenRect.put(3, 0, frameSize.width, frameSize.height)
        val approximatedContour = MatOfPoint2f()

        // is this quad
        if (recognizeOuterQuad(contours, approximatedContour) >= 0) {
            if (isDecoratedSrcByContour) {
                Imgproc.drawContours(
                    frame,
                    listOf(MatOfPoint(*approximatedContour.toArray())),
                    0, CONTOUR_COLOR, 8, Imgproc.LINE_AA
                )
            }
            Log.d(
                TAG,
                " approximatedContour " + approximatedContour.rows() + " " + approximatedContour.cols()
            )
            val transformation = Imgproc.getPerspectiveTransform(approximatedContour, screenRect)
            Log.d(TAG, " approximated contour " + approximatedContour.dump())
            Log.d(TAG, " 1 " + approximatedContour[0, 0][0])
            Log.d(TAG, " 2 " + approximatedContour[1, 0][0])
            Log.d(TAG, " 3 " + approximatedContour[2, 0][0])
            Log.d(TAG, " 4 " + approximatedContour[3, 0][0])
            Imgproc.warpPerspective(frame, outFrame, transformation, frameSize)

            return outFrame
        } else {
            return null
        }
    }

    fun saveImage(img: Mat, context: Context) {
        val path = context.filesDir
        val file = File(path, "img0.jpg")
        Imgcodecs.imwrite(file.absolutePath, img);
    }
}