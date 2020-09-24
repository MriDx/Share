package com.mridx.shareshit.thread

import android.util.Log
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ConnectionRetriever(val serverSocket: ServerSocket) : Thread() {

    private lateinit var client: Socket

    var onConnectionReceived: ((connected: Boolean, clientIp: String?, clientPort: Int) -> Unit)? =
        null


    override fun run() {
        super.run()
        try {
            do {
                client = serverSocket.accept()
                val ip = client.inetAddress.hostAddress
                onConnectionReceived?.invoke(true, ip, 0)

            } while (true)
        } catch (e: IOException) {
            e.printStackTrace()
            onConnectionReceived?.invoke(false, null, 0)
        }
    }


}