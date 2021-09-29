package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CameraCapture extends CameraActivity implements CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Net net;
    private BaseLoaderCallback mLoaderCallback;
    static final String[] classNames = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};

    {
        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.d(TAG, "OpenCV loaded successfully");
                        mOpenCvCameraView.enableView();
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_capture);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG, "onCameraViewStarted");

        String proto = getPath("MobileNetSSD_deploy.prototxt", this);
        String weights = getPath("MobileNetSSD_deploy.caffemodel", this);
        //net = Dnn.readNetFromCaffe(proto, weights);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        /*final int IN_WIDTH = 300;
        final int IN_HEIGHT = 300;
        final float WH_RATIO = (float) IN_WIDTH / IN_HEIGHT;
        final double IN_SCALE_FACTOR = 0.007843;
        final double MEAN_VAL = 127.5;
        final double THRESHOLD = 0.5;

        // Get a new frame
        Mat frame = inputFrame.rgba();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);
        // Forward image through network.
        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
                new Size(IN_WIDTH, IN_HEIGHT),
                new Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), false, false);
        net.setInput(blob);
        Mat detections = net.forward();
        int cols = frame.cols();
        int rows = frame.rows();
        detections = detections.reshape(1, (int) detections.total() / 7);
        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > THRESHOLD) {
                int classId = (int) detections.get(i, 1)[0];
                int left = (int) (detections.get(i, 3)[0] * cols);
                int top = (int) (detections.get(i, 4)[0] * rows);
                int right = (int) (detections.get(i, 5)[0] * cols);
                int bottom = (int) (detections.get(i, 6)[0] * rows);
                // Draw rectangle around detected object.
                Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom),
                        new Scalar(0, 255, 0));
                if (classId >= 0 && classId < classNames.length) {
                    String label = classNames[classId] + ": " + confidence;
                    int[] baseLine = new int[1];
                    Size labelSize = Imgproc.getTextSize(label, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
                    // Draw background for label.
                    Imgproc.rectangle(frame, new Point(left, top - labelSize.height),
                            new Point(left + labelSize.width, top + baseLine[0]),
                            new Scalar(255, 255, 255), Imgproc.FILLED);
                    // Write class name and confidence.
                    Imgproc.putText(frame, label, new Point(left, top),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));
                }
            }
        }*/
        Mat frame = inputFrame.rgba();
        final Size frameSize = frame.size();
        Mat blurred = Mat.zeros(frame.size(), frame.type());
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.bilateralFilter(frame, blurred, 9, 25, 25);
        Imgproc.Canny(blurred, frame, 30, 200);

        Log.d(TAG, " frame.size() " + frame.size());

        Mat mHierarchy = new Mat();
        Scalar CONTOUR_COLOR = new Scalar(0,255,0,255);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(frame, contours, mHierarchy, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);

        final var largest_area = new Object() { double area; int index;};
        AtomicInteger index = new AtomicInteger(0);
        contours.forEach(
                contour -> {
                    double area = Imgproc.contourArea(contour);
                    if(largest_area.area < area)
                    {
                        largest_area.area = area;
                        largest_area.index = index.get();
                    }
                    index.getAndIncrement();
                    Log.d(TAG, " largest_area " + largest_area.index + " " + area);
                }
        );

        Log.d(TAG, " largest_area " + largest_area.index + " " + contours.size());
        //Imgproc.drawContours(inputFrame.rgba(), contours, largest_area.index, CONTOUR_COLOR, 8, Imgproc.LINE_AA);

        final var contour = contours.get(largest_area.index);
        Imgproc.cvtColor(inputFrame.rgba(), frame, Imgproc.COLOR_BGR2RGBA);

        var perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
        var approx = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approx, 0.02 * perimeter, true);

        // is this quad
        if( approx.rows() == 4) {
            final Rect boundingRect = Imgproc.boundingRect(approx);

            Log.d(TAG, " boundingRect " + boundingRect);
            //Imgproc.drawContours(inputFrame.rgba(), new ArrayList<>(List.of(new MatOfPoint(approx.toArray()))), 0, CONTOUR_COLOR, 8, Imgproc.LINE_AA);

            /*Mat rotatedRect = new Mat();
            Imgproc.boxPoints(Imgproc.minAreaRect(new MatOfPoint2f(approx.toArray())), rotatedRect);*/

            Log.d(TAG, " approx " + approx.rows() + " " + approx.cols());

            /*Mat transformation = Imgproc.getAffineTransform(
                    new MatOfPoint2f(new Point(approx.get(0, 0)),
                                     new Point(approx.get(1, 0)),
                                     new Point(approx.get(2, 0))),
                    Utils.convertRectToMatOfPoint(boundingRect));*/

            Mat rect = new Mat(4, 1, CvType.CV_32FC2);
            rect.put(0, 0, frameSize.width, 0);
            rect.put(1, 0, 0, 0);
            rect.put(2, 0, 0, frameSize.height);
            rect.put(3, 0, frameSize.width, frameSize.height);
            Mat transformation = Imgproc.getPerspectiveTransform(approx, rect);

            Log.d(TAG, " rotatedRect " + approx.dump());
            Log.d(TAG, " 1 " + approx.get(0, 0)[0]);
            Log.d(TAG, " 2 " + approx.get(1, 0)[0]);
            Log.d(TAG, " 3 " + approx.get(2, 0)[0]);
            Log.d(TAG, " 4 " + approx.get(3, 0)[0]);

            //Imgproc.warpAffine(frame, frame, transformation, frame.size());
            Imgproc.warpPerspective(frame,frame, transformation, frame.size());

        /*Mat image_roi = new Mat(frame, boundingRect);
        Imgproc.warpAffine(image_roi, image_roi, transformation, image_roi.size());
        Imgproc.resize(image_roi, image_roi, frame.size());*/

            return frame;
        }
        else {
            return inputFrame.rgba();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }
}
