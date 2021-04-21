package com.zzs.n1.ui

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zzs.n1.utils.showToast
import com.zzs.n1.widget.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
@author  zzs
@Date 2021/4/21
@describe
 */

private var dialog:LoadingDialog?=null


fun Fragment.showLoading(msg:String="正在配对..."){
    if (dialog==null){
        dialog = LoadingDialog(requireContext())
    }
    if (dialog?.isShowing==true){
        dialog?.setMsg(msg)
    }else{
        dialog?.setMsg(msg)
        dialog?.show()
    }
}

fun Fragment.showToast(msg:String){
    Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(msg:String){
    Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
}


fun Fragment.hideLoading(){
    dialog?.dismiss()
}

fun ViewModel.launchIO(bl:()->Unit){
    viewModelScope.launch(Dispatchers.IO){
        kotlin.runCatching {

            bl.invoke()
        }.onFailure {
            showToast(it.message?:"")
        }

    }
}