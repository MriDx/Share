package com.mridx.shareshit.util

import java.text.DecimalFormat

class Utils {

    enum class TYPE {
        HOST, CLIENT
    }

    enum class FileSenderType {
        APP, MUSIC, VIDEO, FOLDER
    }


    companion object {
        var HOST_IP = "192.168.43.1"
        const val HOST_PORT = 7575
        const val CONNECT_HOST_PORT = 7574
        var CLIENT_IP = ""
        var CLIENT_PORT = HOST_PORT.plus(1)

        const val MB = 1024 * 1024.toLong()
        const val KB: Long = 1024
        val decimalFormat = DecimalFormat("#.##")
    }

}