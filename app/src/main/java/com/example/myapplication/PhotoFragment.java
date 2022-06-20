package com.example.myapplication;

import androidx.lifecycle.ViewModelProvider;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.core.Mat;
import org.opencv.android.Utils;

public class PhotoFragment extends Fragment {

    private ScannerViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        View view = inflater.inflate(R.layout.photo_fragment, container, false);

        Mat snapShot = mViewModel.getFrame();
        Bitmap bitMap = Bitmap.createBitmap(snapShot.cols(), snapShot.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(snapShot, bitMap);
        ImageView imgViewBefore = (ImageView) view.findViewById(R.id.imgViewBefore);
        imgViewBefore.setImageBitmap(bitMap);

        Mat processedMat = mViewModel.makeScanning(snapShot, true);
        bitMap = Bitmap.createBitmap(processedMat.cols(), processedMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(processedMat, bitMap);
        ImageView imgViewAfter = (ImageView) view.findViewById(R.id.imgViewAfter);
        imgViewAfter.setImageBitmap(bitMap);

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        return view;
    }

}