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

                if (client.getInputStream() != null) {
                    val bufferedInputStream = BufferedInputStream(client.getInputStream())

                    val dataInputStream = DataInputStream(bufferedInputStream)

                    var clientIp = dataInputStream.readUTF() //read ip

                    clientIp = client.inetAddress.hostAddress

                    val clientPort = dataInputStream.readInt() //read port

                    onConnectionReceived?.invoke(true, clientIp, clientPort)

                    client.close()
                }
            } while (true)
        } catch (e: IOException) {
            e.printStackTrace()
            onConnectionReceived?.invoke(false, null, 0)
        }
    }


}