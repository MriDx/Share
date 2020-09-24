package com.mridx.shareshit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mridx.shareshit.data.FileSenderData
import com.mridx.shareshit.data.ProgressData
import com.mridx.shareshit.thread.FileReceiver
import com.mridx.shareshit.thread.FileSender
import com.mridx.shareshit.thread.callback.SenderThreadCallback
import com.mridx.shareshit.util.Utils

class MainUIViewModel : ViewModel(), SenderThreadCallback {


    lateinit var fileReceiver: FileReceiver
    lateinit var userType: Utils.TYPE

    private var _totalSent = MutableLiveData<Long>()
    val totalSent = _totalSent

    private var _transferSuccess = MutableLiveData<Boolean>()
    val transferSuccess = _transferSuccess

    private var _progress = MutableLiveData<ProgressData>()
    val progress = _progress

    fun setup(userType: Utils.TYPE) {
        this.userType = userType
        fileReceiver = FileReceiver(userType)
        fileReceiver.onDownloding = this::onDownloading
        fileReceiver.start()
    }

    private fun onDownloading(name: String, size: Long, progress: Long) {
        _progress.postValue(ProgressData(name, size, progress))
    }

    fun onSendAction(list: ArrayList<Any>, fileSenderType: Utils.FileSenderType) {
        val fileSender = FileSender(
            FileSenderData(
                list,
                fileSenderType,
                userType,
                if (userType === Utils.TYPE.CLIENT) Utils.HOST_IP else Utils.CLIENT_IP
            )
        )
        fileSender.senderThreadCallback = this
        fileSender.start()
    }

    override fun onProgress(name: String, length: Long, progress: Long) {
        _totalSent.postValue(progress)
    }

    override fun onComplete() {
        _transferSuccess.postValue(true)
    }

}