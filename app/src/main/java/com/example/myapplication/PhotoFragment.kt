package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.PhotoFragmentBinding
import org.opencv.core.Core
import org.opencv.android.Utils as OpenCVUtils


class PhotoFragment : Fragment() {
    private lateinit var mViewModel: ScannerViewModel
    private lateinit var binding: PhotoFragmentBinding
    private lateinit var mImagePath: String
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(ScannerViewModel::class.java)
        binding = PhotoFragmentBinding.inflate(layoutInflater, container, false)
        mViewModel.liveData().observe(viewLifecycleOwner, Observer { snapShot ->
            Log.d(Utils.TAG, " frame processing with number of cols: " + snapShot.cols() + " rows: " + snapShot.rows())
            if (snapShot.cols() > 0 && snapShot.rows() > 0) {
                var bitMap = Bitmap.createBitmap(snapShot.cols(), snapShot.rows(), Bitmap.Config.ARGB_8888)
                OpenCVUtils.matToBitmap(snapShot, bitMap)
                val imgViewBefore = binding.imgViewBefore
                imgViewBefore.setImageBitmap(bitMap)
                Utils.makeScanning(snapShot, false)?.let {
                    bitMap =
                        Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
                    OpenCVUtils.matToBitmap(it, bitMap)
                    val imgViewAfter = binding.imgViewAfter
                    imgViewAfter.setImageBitmap(bitMap)
                    mImagePath = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        bitMap,
                        "ScannedImage",
                        null
                    )
                }
            } else {
                Log.e(Utils.TAG, " invalid frame processed: ")
            }
        })

        binding.saveButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "application/image"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("eamalafeev@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scanned img")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "From Scanner app")
            val uri: Uri = Uri.parse(mImagePath)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        }

        return binding.root
    }
}