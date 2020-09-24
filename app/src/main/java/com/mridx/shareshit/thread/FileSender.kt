package com.mridx.shareshit.thread

import com.mridx.shareshit.data.FileSenderData
import com.mridx.shareshit.thread.callback.SenderThreadCallback
import com.mridx.shareshit.util.Utils
import java.io.IOException
import java.net.Socket

class FileSender(private val fileSenderData: FileSenderData) : Thread(), SenderThreadCallback {

    var senderThreadCallback : SenderThreadCallback? = null

    override fun run() {
        super.run()

        try {
            val socket =
                if (fileSenderData.type == Utils.TYPE.CLIENT) {
                    Socket(Utils.HOST_IP, Utils.HOST_PORT)
                } else {
                    Socket(Utils.CLIENT_IP, Utils.CLIENT_PORT)
                }
            SenderThread(fileSenderData, socket).also {
                it.senderThreadCallback = this
                it.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onProgress(name: String, length: Long, progress: Long) {
        senderThreadCallback?.onProgress(name, length, progress)
    }

    override fun onComplete() {
        senderThreadCallback?.onComplete()
    }

}