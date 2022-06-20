package com.example.myapplication

import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.ArrayList

object Utils {
    const val TAG = "Scanner"
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
}