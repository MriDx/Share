package com.mridx.shareshit.data

import android.graphics.drawable.Drawable

data class AppData(
    val appName: String?,
    val appIcon: Drawable?,
    val apkPath: String?,
    val apkSize: String?,
    var selected: Boolean
)