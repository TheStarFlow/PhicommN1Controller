package com.zzs.n1.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzs.n1.BluetoothControlViewModel
import com.zzs.n1.databinding.DialogDeviceBinding

/**
@author  zzs
@Date 2021/4/19
@describe
 */
class BlueDeviceListDialog : DialogFragment(), DeviceAdapter.OnItemClickListener {

    private lateinit var binding: DialogDeviceBinding
    private lateinit var adapter: DeviceAdapter
    private lateinit var blAdapter: BluetoothAdapter
    private val devices by lazy { ArrayList<BluetoothDevice>() }
    private val broadcastReceiver = BlueDeviceBroadcastReceiver()
    private lateinit var blueViewModel:BluetoothControlViewModel
    companion object {
        fun newInstance(): BlueDeviceListDialog {
            return BlueDeviceListDialog()
        }

        const val TAG = "BlueDeviceListDialog"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DeviceAdapter(devices, this)
        binding.deviceRv.layoutManager = LinearLayoutManager(requireContext())
        binding.deviceRv.adapter = adapter
        binding.deviceRv.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        findPairedDevices()
        searchDevice()
        blueViewModel = ViewModelProvider(parentFragment as ViewModelStoreOwner)[BluetoothControlViewModel::class.java]

    }

    private fun searchDevice() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireContext().registerReceiver(broadcastReceiver, filter)
        blAdapter.startDiscovery()
    }

    private fun findPairedDevices() {
        blAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairDevices = blAdapter.bondedDevices
        devices.addAll(pairDevices)
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            val attrs = attributes
            attributes.width = (Resources.getSystem().displayMetrics.widthPixels*0.9f).toInt()
            attributes.height =  (Resources.getSystem().displayMetrics.heightPixels*0.6f).toInt()
            attributes = attrs
        }
    }



    inner class BlueDeviceBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.run {
                        adapter.addItem(this)
                        Log.d(TAG, "found device ${device.toString()}")
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
        blAdapter.cancelDiscovery()
    }

    override fun onItemClick(item: BluetoothDevice) {
        blueViewModel.device.value = item
        dismiss()
    }
}