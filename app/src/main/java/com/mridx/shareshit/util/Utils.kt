package com.mridx.shareshit.util

class Utils {

    enum class TYPE {
        HOST, CLIENT
    }

    companion object {
        const val HOST_IP = "192.168.43.1"
        const val HOST_PORT = 7575
        const val CONNECT_HOST_PORT = 7574
        var CLIENT_IP = null
        var CLIENT_PORT = HOST_PORT.plus(1)
    }

}