package com.zzs.n1.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.zzs.n1.R
import com.zzs.n1.databinding.ItemDeviceBinding

/**
@author  zzs
@Date 2021/4/19
@describe
 */
class DeviceAdapter(private val data: MutableList<BluetoothDevice>, private val l: OnItemClickListener) :
    RecyclerView.Adapter<DeviceAdapter.DViewHolder>() {

    class DViewHolder(val binding: ItemDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DViewHolder {
        val binding = DataBindingUtil.inflate<ItemDeviceBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_device,
            parent,
            false
        )
        val holder = DViewHolder(binding)
        holder.binding.root.setOnClickListener {
            val position = holder.layoutPosition
            l.onItemClick(data[position])
        }
        return holder
    }

    @SuppressLint("SetTextI18n", "UseCompatTextViewDrawableApis")
    override fun onBindViewHolder(holder: DViewHolder, position: Int) {
        holder.binding.deviceName.text = "${data[position].name?:"匿名设备"}\n(${data[position].address})"
        if (data[position].bondState==BluetoothDevice.BOND_BONDED){
            holder.binding.deviceName.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#03a9f4"))
        }else{
            holder.binding.deviceName.compoundDrawableTintList = ColorStateList.valueOf(Color.GRAY)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addItem(item:BluetoothDevice){
        if (!data.contains(item)){
            data.add(item)
            notifyItemInserted(data.size-1)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: BluetoothDevice)
    }
}