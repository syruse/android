package com.example.myapplication;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

public class ScannerViewModel extends ViewModel {
    final private Scalar CONTOUR_COLOR = new Scalar(0,255,0,255);
    final private MutableLiveData<Mat> mFrame = new MutableLiveData<>();

    public void setFrame(Mat frame){

        mFrame.postValue(frame);
    }

    public Mat getFrame(){

        return mFrame.getValue();
    }

    public Mat makeScanning(Mat frame, boolean isDecoratedSrcByContour){
        Mat out_frame = frame.clone();
        final Size frameSize = frame.size();
        Log.d(Utils.TAG, " frameSize: " + frameSize);

        var contours = Utils.findContours(frame);

        Mat screenRect = new Mat(4, 1, CvType.CV_32FC2);
        screenRect.put(0, 0, frameSize.width, 0);
        screenRect.put(1, 0, 0, 0);
        screenRect.put(2, 0, 0, frameSize.height);
        screenRect.put(3, 0, frameSize.width, frameSize.height);

        MatOfPoint2f approximatedContour = new MatOfPoint2f();

        // is this quad
        if( Utils.recognizeOuterQuad(contours, approximatedContour) >= 0) {
            if(isDecoratedSrcByContour) {
                Imgproc.drawContours(frame,
                        new ArrayList<>(Arrays.asList(new MatOfPoint(approximatedContour.toArray()))),
                        0, CONTOUR_COLOR, 8, Imgproc.LINE_AA);
            }

            Log.d(Utils.TAG, " approximatedContour " + approximatedContour.rows() + " " + approximatedContour.cols());

            Mat transformation = Imgproc.getPerspectiveTransform(approximatedContour, screenRect);

            Log.d(Utils.TAG, " approximated contour " + approximatedContour.dump());
            Log.d(Utils.TAG, " 1 " + approximatedContour.get(0, 0)[0]);
            Log.d(Utils.TAG, " 2 " + approximatedContour.get(1, 0)[0]);
            Log.d(Utils.TAG, " 3 " + approximatedContour.get(2, 0)[0]);
            Log.d(Utils.TAG, " 4 " + approximatedContour.get(3, 0)[0]);

            Imgproc.warpPerspective(frame, out_frame, transformation, frameSize);
        }

        return out_frame;
    }
}