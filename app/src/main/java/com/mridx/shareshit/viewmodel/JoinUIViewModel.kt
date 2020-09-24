package com.mridx.shareshit.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Handler
import android.text.format.Formatter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.mridx.shareshit.data.ConnectionData
import com.mridx.shareshit.thread.ConnectionSender
import com.mridx.shareshit.util.PermissionHandler
import com.mridx.shareshit.util.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class JoinUIViewModel : ViewModel() {

    private var ssid: String? = null
    private var scanner: IntentIntegrator? = null
    private var connectionSender: ConnectionSender? = null

    private val _connectionInfo =
        MutableLiveData<ConnectionData>().apply { value = ConnectionData(false, null) }

    val connectionInfo = _connectionInfo

    private val _showPushBtn = MutableLiveData<Boolean>().apply { value = false }
    val showPushBtn = _showPushBtn

    fun startScanner(context: Context) {
        if (!PermissionHandler.checkLocation(context)) {
            PermissionHandler.askLocation(context)
            return
        }
        if (!PermissionHandler.isLocationEnabled(context)) {
            PermissionHandler.askToEnableLocation(context)
            return
        }
        if (!PermissionHandler.checkCamera(context)) {
            PermissionHandler.askCamera(context)
            return
        }
        if (scanner == null)
            scanner = IntentIntegrator(context as Activity?)
        scanner?.setOrientationLocked(true)
        scanner?.setPrompt("Scan QR code to connect")
        scanner?.initiateScan()
    }

    fun parseResult(context: Context, wifiManager: WifiManager, contents: String) {
        if (contents.contains("/")) {
            val data: Array<String> = contents.split("/".toRegex()).toTypedArray()
            val name = data[0].also { ssid = it }
            val password = data[1]
            val configuration: WifiConfiguration? = getWifiConfig(wifiManager, name)
            if (configuration == null) {
                createWPAProfile(context, name, password, wifiManager)
            } else {
                wifiManager.disconnect()
                wifiManager.enableNetwork(configuration.networkId, true)
                wifiManager.reconnect()
                startCheckLoop(wifiManager)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getWifiConfig(
        wifiManager: WifiManager,
        name: String
    ): WifiConfiguration? {

        val configurationList: List<WifiConfiguration>? = wifiManager.configuredNetworks
        if (configurationList != null) {
            for (wifiConfiguration in configurationList) {
                if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals(
                        name,
                        ignoreCase = false
                    )
                ) {
                    return wifiConfiguration
                }
            }
        }
        return null
    }

    @Synchronized
    private fun createWPAProfile(
        context: Context,
        name: String,
        password: String,
        wifiManager: WifiManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val builder = WifiNetworkSpecifier.Builder()
            builder.setSsid(name)
            builder.setWpa2Passphrase(password)
            val wifiNetworkSpecifier = builder.build()
            val networkRequestBuilder = NetworkRequest.Builder()
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier)
            val networkRequest = networkRequestBuilder.build()
            val cm = context.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.requestNetwork(networkRequest, object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    cm.bindProcessToNetwork(network)
                    //goToFiles();
                    startCheckingHost(wifiManager)
                }
            })
            return
        }
        val configuration = WifiConfiguration()
        configuration.SSID = "\"" + name + "\"" /*name;*/
        configuration.preSharedKey = "\"" + password + "\"" /*password;*/
        configuration.status = WifiConfiguration.Status.ENABLED
        val networkId: Int = wifiManager.addNetwork(configuration)
        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()
        GlobalScope.launch {
            delay(1000 * 3)
            startCheckLoop(wifiManager)
        }
        //startCheckLoop(wifiManager)
    }

    private fun startCheckLoop(wifiManager: WifiManager) {
        val name = wifiManager.connectionInfo.ssid
        if (name.contains("\"") && name.replace("\"", "").equals(ssid, ignoreCase = false)) {
            startCheckingHost(wifiManager)
        } /*else {
            GlobalScope.launch {
                delay(1000)
                startCheckLoop(wifiManager)
            }
        }*/
        GlobalScope.launch {
            delay(1000 * 3)
            _showPushBtn.postValue(true)
        }
    }

    fun startCheckingHost(wifiManager: WifiManager) {
        if (wifiManager.connectionInfo.ssid.replace("\"", "").equals(ssid, ignoreCase = false)) {
            val connectionInfo = wifiManager.connectionInfo
            val ip = Formatter.formatIpAddress(connectionInfo.ipAddress)
            makeRequestToHost(ip)
        }
    }

    private fun makeRequestToHost(ip: String) {
        connectionSender = ConnectionSender(ip)
        connectionSender?.onConnectionOpened = this::onResponse
        connectionSender?.start()
    }

    private fun onResponse(success: Boolean) {
        if (success) {
            _connectionInfo.postValue(ConnectionData(true, Utils.HOST_IP))
            return
        }
        // TODO: 20/09/20 show error
    }

    fun onDestroy() {
        connectionSender?.interrupt()
        connectionSender = null
        ssid = null
    }

}