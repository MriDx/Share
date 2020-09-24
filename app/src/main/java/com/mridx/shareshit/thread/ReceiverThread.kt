package com.mridx.shareshit.thread

import android.os.Environment
import android.util.Log
import java.io.*
import java.net.Socket
import kotlin.math.min

class ReceiverThread(private val socket: Socket) : Thread() {

    var onReceiving: ((name: String, size: Long) -> Unit)? = null

    companion object {
        val extStorage = Environment.getExternalStorageDirectory()
    }

    var onProgress: ((String) -> Unit)? = null
    var onComplete: (() -> Unit)? = null
    var onDownloading: ((String, Long, Long) -> Unit)? = null

    override fun run() {
        super.run()
        try {
            if (socket.getInputStream() != null) {
                saveFiles()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveFiles() {

        val bufferedInputStream = BufferedInputStream(socket.getInputStream())
        val dataInputStream = DataInputStream(bufferedInputStream)

        val dataType: String = dataInputStream.readUTF() //read data type
        val totalFiles: Int = dataInputStream.readInt() //read files number
        val files = arrayOfNulls<File>(totalFiles)

        for (i in 0 until totalFiles) {
            val length = dataInputStream.readLong() //file size
            val path = dataInputStream.readUTF().replace("./", "/") //file path
            val name = dataInputStream.readUTF() //file name
            var dir: File
            dir =
                if (dataType == "music" || dataType == "app" || dataType == "photo" || dataType == "video") {
                    File("${extStorage}/mshare/${dataType}", "")
                } else
                    File("${extStorage}/mshare/${dataType}", path)
            if (!dir.exists()) dir.mkdirs()
            if (dataType == "app")
                files[i] = File(dir, "$name.apk")
            else
                files[i] = File(dir, name)
            val fileOutputStream = FileOutputStream(files[i])
            val bufferOutputStream = BufferedOutputStream(fileOutputStream)

            onReceiving?.invoke(name, length)

            var downloaded: Long = 0
            var count = 0

            var temp : Long = 0

            for (j in 0 until length) {
                bufferOutputStream.write(bufferedInputStream.read().also {
                    count = it
                })
                //downloaded = downloaded.plus(count)
                //onDownloading?.invoke(name, length, j) // TODO: 24/09/20 uncomment this line if you need progress
            }

            bufferOutputStream.close()
            onProgress?.invoke(name)
        }
        onComplete?.invoke()

    }

}