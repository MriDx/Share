package com.mridx.shareshit.thread

import android.util.Log
import com.mridx.shareshit.util.Utils
import java.io.IOException
import java.net.Proxy
import java.net.Socket

class ConnectionSender(private val ip: String) : Thread() {

    var onConnectionOpened: ((success: Boolean) -> Unit)? = null

    override fun run() {
        super.run()

        try {
            Socket(getIp(ip), 7574)
            onConnectionOpened?.invoke(true)
        } catch (e: IOException) {
            e.printStackTrace()
            onConnectionOpened?.invoke(false)
        }


    }

    private fun getIp(ip: String): String {
        return "${ip.substring(0, ip.lastIndexOf("."))}.1".also {
            Utils.HOST_IP = it
        }
    }


}