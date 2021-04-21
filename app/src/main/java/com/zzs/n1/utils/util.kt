package com.zzs.n1.utils

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.Toast
import com.zzs.n1.App

/**
@author  zzs
@Date 2021/4/21
@describe
 */

var toast:Toast?=null
@SuppressLint("ShowToast")
fun showToast(msg:String){
    if (toast==null){
        toast = Toast.makeText(App.application,msg,Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER,0,0)
    }else{
        toast?.setText(msg)
    }
    toast?.show()

}