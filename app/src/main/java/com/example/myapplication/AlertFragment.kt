package com.example.myapplication

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AlertFragment(msg: String?) : DialogFragment() {
    private val mMsg: String = msg ?:"wrong credentials"
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setTitle("Error!")
            .setMessage(mMsg)
            .setIcon(R.drawable.ic_baseline_error)
            .setPositiveButton("ОК") { dialog, _ -> dialog.cancel() }
        return builder.create()
    }
}