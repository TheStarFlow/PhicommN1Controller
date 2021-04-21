package com.zzs.n1.ui

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zzs.n1.WifiControlViewModel
import com.zzs.n1.databinding.FragmentWifiControlBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class WifiFragment : Fragment() {
    companion object {
        fun newInstance(): WifiFragment {
            return WifiFragment()
        }
    }

    private lateinit var binding: FragmentWifiControlBinding
    private lateinit var viewModel: WifiControlViewModel
    private lateinit var ipEditText: EditText
    private lateinit var inputDialog :AlertDialog
    var ip = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWifiControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[WifiControlViewModel::class.java]
        binding.model = viewModel
        binding.setIp.setOnClickListener {
            if (!this::ipEditText.isInitialized) {
                initIpEd()
            }
            if (!this::inputDialog.isInitialized){
                inputDialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(ipEditText)
                    .setTitle("请输入盒子的 IP 地址")
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            if (ip.isEmpty()) {
                                showToast("ip 地址不能为空")
                                return
                            }
                            if (!checkIp(ip)){
                                showToast("ip 地址不合法")
                                return
                            }
                            viewModel.updateIp(ip)
                            dialog?.dismiss()


                        }
                    }).setNegativeButton("取消", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    }).create()
            }
            ipEditText.setText("")
            inputDialog.show()
        }
        viewModel.updateIp()
    }

    private fun initIpEd() {
        ipEditText = EditText(requireContext())
        ipEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                ip = ipEditText.text.toString()
            }
        })
    }

    private fun checkIp(ipStr: String): Boolean {
        val ip = ("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$")
        val pattern: Pattern = Pattern.compile(ip)
        val matcher: Matcher = pattern.matcher(ipStr)
        return matcher.matches()
    }
}