package com.zzs.n1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zzs.n1.WifiControlViewModel
import com.zzs.n1.databinding.FragmentWifiControlBinding

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class WifiFragment:Fragment() {
    companion object{
        fun newInstance():WifiFragment {
            return WifiFragment()
        }
    }
    private lateinit var binding:FragmentWifiControlBinding
    private lateinit var viewModel:WifiControlViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWifiControlBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[WifiControlViewModel::class.java]
        binding.model = viewModel
    }
}