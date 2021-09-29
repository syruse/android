package com.example.myapplication;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static MatOfPoint2f convertRectToMatOfPoint(Rect rect){
        List<Point> points = new ArrayList<>();
        /*points.add(new Point(rect.x + rect.width, rect.y));
        points.add(new Point(rect.x, rect.y));
        points.add(new Point(rect.x, rect.y + rect.height));*/
        points.add(new Point(1280-1, 0));
        points.add(new Point(0, 0));
        points.add(new Point(0, 960-1));
        MatOfPoint2f mPoints = new MatOfPoint2f();
        mPoints.fromList(points);

        return mPoints;
    }
}
