package com.zzs.n1.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.zzs.n1.MainViewModel
import com.zzs.n1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val BLUETOOTH_REQ_CODE = 101
    }

    private lateinit var blAdapter: BluetoothAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel :MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkPermission()
        setContentView(binding.root)
        blAdapter = BluetoothAdapter.getDefaultAdapter()
        setUpBluetooth(blAdapter)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initView()
    }

    private fun initView() {
        binding.vp2.adapter = ViewPager2Adapter(this)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1001)
        }
    }

    private fun setUpBluetooth(blDevice: BluetoothAdapter) {
        if (!blDevice.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, BLUETOOTH_REQ_CODE)
            return
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLUETOOTH_REQ_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    showToast("成功开启蓝牙~")
                }
                else -> {
                    showToast("我没办法在不开蓝牙的情况下帮你办事~")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fitSystemBar(window)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }




    private fun fitSystemBar(window: Window) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }.decorView.apply {
            //View.SYSTEM_UI_FLAG_FULLSCREEN = 4.4系统 WindowManager.LayoutParams.FLAG_FULLSCREEN 会隐藏状态栏
            //布局延伸至状态栏  但是不隐藏
            systemUiVisibility =
//                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  //稳定的布局。不管导航栏可见与否都不影响 布局是否在状态栏之下
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //让状态栏文字自适应颜色  变成白底黑字
        }
    }

}