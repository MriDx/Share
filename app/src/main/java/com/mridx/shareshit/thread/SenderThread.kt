package com.mridx.shareshit.thread

import android.util.Log
import com.mridx.shareshit.data.AppData
import com.mridx.shareshit.data.FileSendData
import com.mridx.shareshit.data.FileSenderData
import com.mridx.shareshit.thread.callback.SenderThreadCallback
import com.mridx.shareshit.util.Utils
import java.io.*
import java.net.Socket

class SenderThread(private val fileSenderData: FileSenderData, private val socket: Socket) :
    Thread() {

    var senderThreadCallback: SenderThreadCallback? = null

    companion object {
        var dataType: String? = null
    }

    override fun run() {
        super.run()

        val files = getAllFiles()

        sendFiles(files)

    }

    private fun sendFiles(files: ArrayList<FileSendData>) {
        try {
            val bos = BufferedOutputStream(socket.getOutputStream())
            val dos = DataOutputStream(bos)

            dos.writeUTF(dataType) //write data type
            dos.writeInt(files.size) //total files number

            for (fileData in files) {
                val file = File(fileData.path)
                val size = file.length()
                dos.writeLong(size) //file size

                dos.writeUTF(file.path) //file path

                dos.writeUTF(fileData.name) //file name

                val fileInputStream = FileInputStream(file)
                val bufferedInputStream = BufferedInputStream(fileInputStream)

                var theByte: Int = 0

                val buf = ByteArray(1024)
                var len: Int = 0;
                var sent: Long = 0
                while (bufferedInputStream.read(buf).also {
                        len = it
                        sent = sent.plus(len)
                    } != -1) {
                    bos.write(buf, 0, len)
                }
                bufferedInputStream.close()
            }

            dos.close()
            senderThreadCallback?.onComplete()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getAllFiles(): ArrayList<FileSendData> {
        return when (fileSenderData.fileSenderType) {
            Utils.FileSenderType.APP -> getApps(fileSenderData.list)
            Utils.FileSenderType.MUSIC -> getMusic(fileSenderData.list)
            Utils.FileSenderType.VIDEO -> getVideos(fileSenderData.list)
            Utils.FileSenderType.FOLDER -> getFolders(fileSenderData.list)
            else -> getApps(fileSenderData.list)
        }
    }


    private fun getFolders(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        TODO("Not yet implemented")
    }

    private fun getVideos(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        dataType = "video"
        val datalist: ArrayList<FileSendData> = ArrayList()
        /*for (videoData: VideoData in list as ArrayList<VideoData>) {
            datalist.add(FileSendData(videoData.path, videoData.title))
        }*/
        return datalist
    }

    private fun getMusic(list: java.util.ArrayList<Any>): java.util.ArrayList<FileSendData> {
        dataType = "music"
        val dataList: ArrayList<FileSendData> = ArrayList()
        /*for (musicData: MusicData in list as ArrayList<MusicData>) {
            dataList.add(FileSendData(musicData.path, musicData.title))
        }*/
        return dataList
    }

    private fun getApps(list: ArrayList<Any>): ArrayList<FileSendData> {
        dataType = "app"
        val applist: ArrayList<FileSendData> = ArrayList()
        (list as ArrayList<AppData>).map {
            applist.add(FileSendData(it.apkPath!!, it.appName!!))
        }
        return applist
    }


}

