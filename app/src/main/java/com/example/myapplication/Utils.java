package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    public static final String TAG = "Scanner";

    public static MatOfPoint2f convertRectToMatOfPoint(Rect rect){
        MatOfPoint2f mPoints = new MatOfPoint2f();
        List<Point> points = new ArrayList<>();

        points.add(new Point(rect.x + rect.width, rect.y));
        points.add(new Point(rect.x, rect.y));
        points.add(new Point(rect.x, rect.y + rect.height));

        mPoints.fromList(points);

        return mPoints;
    }

    public static List<MatOfPoint> findContours(Mat inputFrame){
        Mat frame = inputFrame.clone();
        Mat blurred = Mat.zeros(frame.size(), frame.type());
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

        // calibrated values used generating the best result
        Imgproc.bilateralFilter(frame, blurred, 9, 25, 25);
        Imgproc.Canny(blurred, frame, 30, 200);

        Mat mHierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(frame, contours, mHierarchy, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);

        return contours;
    }

    /// return -1 if not found
    /// out_approximatedContour contains only edge points (4 point for rect)
    public static int recognizeOuterQuad(List<MatOfPoint> contours, @NonNull final MatOfPoint2f out_approximatedContour){

        final var largest_area = new Object() { double area; int index = -1; MatOfPoint2f approximatedContour;};
        AtomicInteger index = new AtomicInteger(0);
        contours.forEach(
                contour -> {
                    double area = Imgproc.contourArea(contour);
                    var perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
                    var approximatedContour = new MatOfPoint2f();
                    Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approximatedContour, 0.02 * perimeter, true);

                    Log.d(Utils.TAG, " area " + area + " " + approximatedContour.rows());
                    /// aside from having larger area it must be rectangular
                    if(largest_area.area < area && approximatedContour.rows() == 4) {
                        largest_area.area = area;
                        largest_area.index = index.get();
                        largest_area.approximatedContour = approximatedContour;

                        Log.d(Utils.TAG, " largest_area " + largest_area.index + " " + area);
                    }
                    index.getAndIncrement();
                }
        );

        out_approximatedContour.fromArray(largest_area.approximatedContour.toArray());
        Log.d(Utils.TAG, " largest_area " + largest_area.index + " " + contours.size());
        //final var contour = contours.get(largest_area.index);
        //Imgproc.drawContours(inputFrame.rgba(), contours, largest_area.index, CONTOUR_COLOR, 8, Imgproc.LINE_AA);

        return largest_area.index;
    }

}
