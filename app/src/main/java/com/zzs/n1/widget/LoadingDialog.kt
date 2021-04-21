package com.zzs.n1.widget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.zzs.n1.databinding.LoadingLayoutBinding

/**
@author  zzs
@Date 2021/4/21
@describe
 */
class LoadingDialog(context: Context) : Dialog(context) {
    private var binding: LoadingLayoutBinding = LoadingLayoutBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)
    }

    fun setMsg(msg:String){
        binding.msg.text = msg
    }
}