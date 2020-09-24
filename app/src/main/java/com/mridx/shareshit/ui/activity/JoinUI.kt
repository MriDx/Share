package com.mridx.shareshit.ui.activity

import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.mridx.shareshit.R
import com.mridx.shareshit.util.Utils
import com.mridx.shareshit.viewmodel.JoinUIViewModel
import kotlinx.android.synthetic.main.join_ui.*

class JoinUI : AppCompatActivity() {


    private lateinit var wifiManager: WifiManager
    lateinit var viewModel: JoinUIViewModel
    private var intentFilter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_ui)

        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        viewModel = ViewModelProvider(this).get(JoinUIViewModel::class.java)

        viewModel.startScanner(this)

        viewModel.connectionInfo.observe(this, {
            if (it.success) {
                Toast.makeText(this, "Connected to ${it.ip}", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainUI::class.java)
                intent.putExtra("TYPE", Utils.TYPE.CLIENT)
                startActivity(intent)
                finish()
            }
        })

        viewModel.showPushBtn.observe(this, {
            if (it)
                pushBtn?.visibility = View.VISIBLE
            else
                pushBtn?.visibility = View.GONE
        })

        pushBtn?.setOnClickListener { viewModel.startCheckingHost(wifiManager) }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // TODO: 20/09/20 wrong qr scanned
                //finish()
            } else {
                viewModel.parseResult(this, wifiManager, result.contents)
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        //unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            registerReceiver(receiver, intentFilter)*/
    }

    override fun onStop() {
        super.onStop()
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            unregisterReceiver(receiver)*/
    }


}