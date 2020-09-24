package com.mridx.shareshit.ui.fragment.app

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mridx.shareshit.data.AppData
import com.mridx.shareshit.util.Utils.Companion.KB
import com.mridx.shareshit.util.Utils.Companion.MB
import com.mridx.shareshit.util.Utils.Companion.decimalFormat
import java.io.File

class AppViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _appList = MutableLiveData<ArrayList<AppData>>()

    val appList = _appList

    fun getApps(context: Context) {
        val getApps = GetApps(context)
        getApps.onComplete = this::onGetApp
        getApps.start()
    }

    private fun onGetApp(list: ArrayList<AppData>) {
        _appList.postValue(list)
    }

    private inner class GetApps(val context: Context) : Thread() {
        var onComplete: ((ArrayList<AppData>) -> Unit)? = null
        override fun run() {
            super.run()
            onComplete?.invoke(installedApps())
        }

        fun installedApps(): ArrayList<AppData> {
            val appList = ArrayList<AppData>()
            val packList = context!!.packageManager.getInstalledPackages(0)
            for (i in packList.indices) {
                val packInfo = packList[i]
                if (packInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    val appName =
                        packInfo.applicationInfo.loadLabel(context!!.packageManager).toString()
                    val icon = packInfo.applicationInfo.loadIcon(context!!.packageManager)
                    val apkPath = packInfo.applicationInfo.sourceDir
                    appList.add(AppData(appName, icon, apkPath, getFileSize(apkPath), false))
                }
            }
            return appList
        }

        private fun getFileSize(apkPath: String): String? {
            val file = File(apkPath)
            if (file.exists()) {
                val fileSize = file.length().toDouble()
                return if (fileSize > MB) {
                    decimalFormat.format(fileSize / MB) + " MB"
                } else decimalFormat.format(fileSize / KB) + " KB"
            }
            return "00 KB"
        }
    }

}