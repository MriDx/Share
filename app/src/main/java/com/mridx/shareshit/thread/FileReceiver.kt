package com.mridx.shareshit.thread

import com.mridx.shareshit.util.Utils
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class FileReceiver(private val type: Utils.TYPE) : Thread() {

    lateinit var serverSocket: ServerSocket
    lateinit var client: Socket

    var connectionReceived : (()->Unit)? = null
    var onDownloding : ((String, Long, Long)->Unit)? = null


    override fun run() {
        super.run()
        try {
            serverSocket =
                if (type == Utils.TYPE.HOST) {
                    ServerSocket(Utils.HOST_PORT)
                } else {
                    ServerSocket(Utils.CLIENT_PORT)
                }

            do {
                client = serverSocket.accept()
                connectionReceived?.invoke()
                val receiverThread = ReceiverThread(client)
                receiverThread.onDownloading = onDownloding
                receiverThread.start()

            } while (true)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}