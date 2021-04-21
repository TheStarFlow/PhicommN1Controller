package com.zzs.n1.utils

import android.widget.Toast
import com.zzs.n1.App

/**
@author  zzs
@Date 2021/4/21
@describe
 */

fun showToast(msg:String){
    Toast.makeText(App.application,msg,Toast.LENGTH_SHORT).show()
}