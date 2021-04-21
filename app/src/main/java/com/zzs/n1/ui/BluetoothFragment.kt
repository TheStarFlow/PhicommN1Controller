package com.zzs.n1.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zzs.n1.BluetoothControlViewModel
import com.zzs.n1.databinding.FragmentBluetoothControlBinding
import com.zzs.n1.utils.SpHelper
import java.util.*

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class BluetoothFragment : Fragment() {

    companion object {
        fun newInstance(): BluetoothFragment {
            return BluetoothFragment()
        }

    }

    private var listDialog: BlueDeviceListDialog? = null
    private lateinit var binding: FragmentBluetoothControlBinding
    private lateinit var viewmodel: BluetoothControlViewModel
    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            val action = intent.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                when (device?.bondState) {
                    BluetoothDevice.BOND_BONDING -> {
                        showLoading("正在配对...")
                    }
                    BluetoothDevice.BOND_BONDED ->{
                        hideLoading()
                        showToast("配对成功")
                        connectBl(device)
                    }
                    BluetoothDevice.BOND_NONE->{
                        hideLoading()
                        showToast("取消配对")
                    }
                }
            }
        }

    }

    private fun connectBl(device: BluetoothDevice) {
        viewmodel.connectBl(device)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.deviceList.setOnClickListener {
            if (listDialog == null) {
                listDialog = BlueDeviceListDialog.newInstance()
                listDialog?.dialog?.setOnDismissListener {
                    listDialog = null
                }
            }
            listDialog?.show(childFragmentManager, "blue")
        }
        viewmodel = ViewModelProvider(this)[BluetoothControlViewModel::class.java]
        binding.model = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        viewmodel.device.observe(viewLifecycleOwner, Observer {
            it?.run {
                pair(this)
            }
        })
        viewmodel.mConnectState.observe(viewLifecycleOwner, Observer {
            when(it){
                0->{
                    showLoading("正在连接...")
                }
                2->{
                    hideLoading()
                    showToast("连接失败~")
                }
            }

        })
        viewmodel.reportRegister.observe(viewLifecycleOwner, Observer {
            if (it==true){
                if (viewmodel.mConnectDevice==null){
                    viewmodel.tryConnectDfBl()
                }
            }else{
                viewmodel.mConnectState.value = 2
            }
        })
        register()
        Log.d("UUID","${UUID.randomUUID()}")
    }

    private fun pair(it: BluetoothDevice) {
        val state = it.bondState
        if (state==BluetoothDevice.BOND_BONDED){
            showToast("该设备已配对")
            connectBl(it)
            return
        }else{
            val connect = it.createBond()
            if (!connect) {
                Toast.makeText(requireContext(), "未知错误，连接失败", Toast.LENGTH_LONG).show()
            } else {
                showLoading()
            }
        }
    }

    private fun register() {
        lifecycle.addObserver(viewmodel)
        viewmodel.register(requireContext().applicationContext)
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        requireActivity().registerReceiver(broadcastReceiver,intentFilter)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }
}