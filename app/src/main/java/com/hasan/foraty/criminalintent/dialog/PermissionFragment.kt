package com.hasan.foraty.criminalintent.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hasan.foraty.criminalintent.model.PermissionMassage
private const val ARG_PERMISSION_MASSAGE="permissionMassage"
class PermissionFragment:DialogFragment() {
    interface callback{
        fun onResponce()
    }
    companion object{
        fun newInstance(permissionMassage: PermissionMassage):PermissionFragment{
            val arg=Bundle().apply {
                putSerializable(ARG_PERMISSION_MASSAGE,permissionMassage)
            }
            return PermissionFragment().apply {
                arguments=arg
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {bundle->
            val permissionMassage=bundle.getSerializable(ARG_PERMISSION_MASSAGE) as PermissionMassage
            return AlertDialog.Builder(requireContext())
                    .setTitle(permissionMassage.title)
                    .setMessage(permissionMassage.massage)
                    .setNegativeButton(permissionMassage.cancelText)
                    { _,_-> dialog?.dismiss() }
                    .setPositiveButton(permissionMassage.okText)
                    {_,_-> (targetFragment as callback).onResponce()}
                    .create()
        }
        return super.onCreateDialog(savedInstanceState)
    }
}