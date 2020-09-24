package com.mridx.shareshit.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.mridx.shareshit.R
import com.mridx.shareshit.ui.activity.CreateUI

class PermissionHandler {


    companion object {

        const val LOCATION_PERMISSION_REQ = 901
        const val SYSTEM_PERMISSION_REQ = 801
        const val APP_SETTINGS_REQ = 802
        const val CAMERA_PERMISSION_REQ = 901

        fun checkLocation(context: Context): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
            return true
        }

        fun askLocation(context: Context) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                showRational(context, context.getString(R.string.locationRational))
                return
            }
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ
            )
        }

        fun isLocationEnabled(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.isLocationEnabled
            } else {
                val mode = Settings.Secure.getInt(
                    context.contentResolver, Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        }


        private fun showRational(context: Context, message: String) {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.apply {
                setMessage(message)

                setButton(
                    AlertDialog.BUTTON_POSITIVE,
                    "Go to settings"
                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }

                setButton(
                    AlertDialog.BUTTON_NEGATIVE,
                    "Cancel"
                ) { dialogInterface: DialogInterface, i: Int ->
                    run {
                        dialogInterface.dismiss()
                        (context as Activity).finish()
                    }
                }

                setCancelable(false)
            }
            alertDialog.show()
        }

        fun askToEnableLocation(context: Context) {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.apply {
                setTitle(context.getString(R.string.askToEnableLocationTitle))
                setMessage(context.getString(R.string.askToEnableLocationMessage))
                setButton(
                    DialogInterface.BUTTON_POSITIVE, "Retry"
                ) { dialogInterface: DialogInterface, i: Int ->
                    kotlin.run {
                        if (isLocationEnabled(context)) {
                            dialogInterface.dismiss()
                            if (context is CreateUI) {
                                context.turnOnHotspot()
                            }
                            return@setButton
                        }
                    }
                }
                setButton(
                    DialogInterface.BUTTON_NEGATIVE, "Cancel"
                ) { dialogInterface: DialogInterface, i: Int ->
                    kotlin.run {
                        dialogInterface.dismiss()
                        (context as Activity).finish()
                    }
                }
            }
            alertDialog.show()
        }

        fun checkCamera(context: Context): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return true
        }

        fun askCamera(context: Context) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CAMERA
                )
            ) {
                showRational(context, context.getString(R.string.locationRational))
                return
            }
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQ
            )
        }

    }


}

