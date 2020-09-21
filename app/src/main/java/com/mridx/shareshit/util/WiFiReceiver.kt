package com.mridx.shareshit.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import com.mridx.shareshit.ui.JoinUI

class WiFiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        //val wifiState: Int = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
        /* if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.getAction() && WifiManager.WIFI_STATE_ENABLED == wifiState) {
             if (Log.isLoggable(TAG, Log.VERBOSE)) {
                 Log.v(TAG, "Wifi is now enabled")
             }
             //context.startService(new Intent(context, WiFiActiveService.class));
         } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED" == action) {
             if (context is JoinUI) {
                 (context as JoinUI).wifiConnected()
             }
         } else if ("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED" == action) {
             Log.d(TAG, "onReceive: client changed")
             *//*if (context instanceof CreateUI) {
                ((CreateUI) context).goToFiles();
            }*//*
        }*/

        if (action == "android.net.wifi.WIFI_AP_STATE_CHANGED") {
            if (context is JoinUI) {
                context.wifiReceived()
            }
        }
    }


}