package com.hasan.foraty.criminalintent.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hasan.foraty.criminalintent.R
import com.hasan.foraty.criminalintent.getScaledBitmap

class PictureDialogFragment :DialogFragment(){

    interface Callback{
        fun getPicture():String
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog=AlertDialog.Builder(requireContext())
        targetFragment?.let {
            val uri=(targetFragment as Callback).getPicture()
            Log.d("TEST","Target get init")
            val bitmap=getScaledBitmap(uri,requireActivity())
            val imageView=ImageView(requireContext())
            imageView.setImageBitmap(bitmap)
            alertDialog.setView(imageView)
        }
        return alertDialog.create()
    }

}