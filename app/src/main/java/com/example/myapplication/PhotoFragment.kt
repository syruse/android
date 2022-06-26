package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.opencv.android.Utils as OpenCVUtils
import com.example.myapplication.databinding.PhotoFragmentBinding

class PhotoFragment : Fragment() {
    private lateinit var mViewModel: ScannerViewModel
    private lateinit var binding: PhotoFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(ScannerViewModel::class.java)
        binding = PhotoFragmentBinding.inflate(layoutInflater, container, false)
        mViewModel.liveData().observe(viewLifecycleOwner, Observer { snapShot ->
            Log.d(Utils.TAG, " frame processing ")
            var bitMap = Bitmap.createBitmap(snapShot.cols(), snapShot.rows(), Bitmap.Config.ARGB_8888)
            OpenCVUtils.matToBitmap(snapShot, bitMap)
            val imgViewBefore = binding.imgViewBefore
            imgViewBefore.setImageBitmap(bitMap)
            Utils.makeScanning(snapShot, true)?.let {
                bitMap =
                    Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
                OpenCVUtils.matToBitmap(it, bitMap)
                val imgViewAfter = binding.imgViewAfter
                imgViewAfter.setImageBitmap(bitMap)
            }
        })

        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return binding.root
    }
}