package com.mridx.shareshit.thread.callback

interface SenderThreadCallback {

    fun onProgress(name: String, length: Long, progress: Long)
    fun onComplete()

}