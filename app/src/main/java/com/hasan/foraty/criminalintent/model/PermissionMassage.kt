package com.hasan.foraty.criminalintent.model

import java.io.Serializable

data class PermissionMassage(val title:String, val massage:String
                             , var okText:String?="ok", var cancelText:String?="cancel"
                             , var response:Boolean?=false)
                            : Serializable