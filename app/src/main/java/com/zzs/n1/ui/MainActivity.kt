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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.zzs.n1.MainViewModel
import com.zzs.n1.R
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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE),1001)
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
                    showToast("??????????????????~")
                }
                else -> {
                    showToast("???????????????????????????????????????????????????~")
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
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.start1)
        }.decorView.apply {
            //View.SYSTEM_UI_FLAG_FULLSCREEN = 4.4?????? WindowManager.LayoutParams.FLAG_FULLSCREEN ??????????????????
            //????????????????????????  ???????????????
            systemUiVisibility =
//                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  //????????????????????????????????????????????????????????? ??????????????????????????????
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //?????????????????????????????????  ??????????????????
        }
    }

}