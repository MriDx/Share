package com.mridx.shareshit.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.mridx.shareshit.data.ConnectionData
import com.mridx.shareshit.thread.ConnectionRetriever
import com.mridx.shareshit.util.PermissionHandler
import com.mridx.shareshit.util.Util
import java.io.IOException
import java.net.ServerSocket

class CreateUIViewModel : ViewModel() {

    //var hotspotReservation: LocalOnlyHotspotReservation? = null
    var connectionRetriever: ConnectionRetriever? = null

    private val _progress = MutableLiveData<Boolean>().apply { value = false }
    private val _qr = MutableLiveData<Bitmap>().apply { value = null }
    private val _connectionInfo = MutableLiveData<ConnectionData>().apply {
        value = ConnectionData(false, null)
    }


    val progress = _progress
    val qr = _qr
    val connectionInfo = _connectionInfo

    @SuppressLint("MissingPermission")
    fun turnOnHotspot(context: Context, wifiManager: WifiManager) {
        if (!PermissionHandler.checkLocation(context)) {
            PermissionHandler.askLocation(context)
            return
        }
        if (!PermissionHandler.isLocationEnabled(context)) {
            PermissionHandler.askToEnableLocation(context)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(object : LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: LocalOnlyHotspotReservation) {
                    super.onStarted(reservation)
                    //hotspotReservation = reservation
                    Util.getInstance().hotspotReservation = reservation
                    generateQR(reservation)
                }

                override fun onStopped() {
                    super.onStopped()
                    Log.d("kaku", "onStopped: ")
                }

                override fun onFailed(reason: Int) {
                    super.onFailed(reason)
                    Log.d("kaku", "onFailed: ")
                }
            }, Handler())
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                (context as Activity).startActivityForResult(
                    intent,
                    PermissionHandler.SYSTEM_PERMISSION_REQ
                )
                return
            }
        }
        val configuration = WifiConfiguration()
        configuration.SSID = "AndroidHotspot-mShare-1"
        configuration.preSharedKey = "9854935115"
        configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        try {
            val setWifiApMethod = wifiManager.javaClass.getMethod(
                "setWifiApEnabled",
                WifiConfiguration::class.java,
                Boolean::class.javaPrimitiveType
            )
            val apstatus = setWifiApMethod.invoke(wifiManager, configuration, true) as Boolean
            if (apstatus) {
                generateQR(configuration)
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateQR(reservation: LocalOnlyHotspotReservation) {
        val configuration = reservation.wifiConfiguration
        val data = configuration!!.SSID + "/" + configuration.preSharedKey
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            startServerSocket()
            sendBmp(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateQR(configuration: WifiConfiguration) {
        val data = configuration.SSID + "/" + configuration.preSharedKey
        val writer = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width: Int = bitMatrix.width
            val height: Int = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            startServerSocket()
            sendBmp(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }


    private fun startServerSocket() {
        try {
            val serverSocket: ServerSocket = Util.getInstance().serverSocket
            connectionRetriever = ConnectionRetriever(serverSocket)
            connectionRetriever?.start()
            connectionRetriever?.onConnectionReceived = this::invoke
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun invoke(connected: Boolean, clientIp: String?, clientPort: Int) {
        kotlin.run {
            /* _connectionInfo.apply {
                 value = ConnectionData(connected, clientIp)
             }*/
            _connectionInfo.postValue(ConnectionData(connected, clientIp))
        }

    }

    fun destroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //hotspotReservation?.close()
            //hotspotReservation = null
        }
        Util.getInstance().serverSocket.close()
        Util.getInstance().serverSocket = null
        connectionRetriever?.interrupt()
        connectionRetriever = null
    }

    private fun sendBmp(bmp: Bitmap?) {
        _qr.apply {
            value = bmp
        }
    }


}



