package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.myapplication.Utils.TAG
import org.opencv.android.Utils
import org.opencv.core.Mat

class PhotoFragment : Fragment() {
    private lateinit var mViewModel: ScannerViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(ScannerViewModel::class.java)
        val view = inflater.inflate(R.layout.photo_fragment, container, false)

        if (mViewModel.frame != null) {
            val snapShot = mViewModel.frame as Mat
            var bitMap = Bitmap.createBitmap(snapShot.cols(), snapShot.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(snapShot, bitMap)
            val imgViewBefore = view.findViewById<View>(R.id.imgViewBefore) as ImageView
            imgViewBefore.setImageBitmap(bitMap)
            val processedMat = mViewModel.makeScanning(snapShot, true)
            if (processedMat != null) {
                bitMap =
                    Bitmap.createBitmap(processedMat.cols(), processedMat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(processedMat, bitMap)
                val imgViewAfter = view.findViewById<View>(R.id.imgViewAfter) as ImageView
                imgViewAfter.setImageBitmap(bitMap)
            } else {
                Log.e(TAG, " !!! couldn't detect quadratic area ")
            }
        } else {
            Log.e(TAG, " !!! invalid image ")
        }

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return view
    }
}