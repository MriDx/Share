package com.mridx.shareshit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.mridx.shareshit.R
import com.mridx.shareshit.data.AppData
import java.util.*
import kotlin.collections.ArrayList

class AppAdapter : RecyclerView.Adapter<AppAdapter.MyViewHolder>() {

    private var appList: ArrayList<AppData> = ArrayList()
    private var selectedAppList: ArrayList<AppData> = ArrayList()

    private val SELECTED = 0
    private val NORMAL: Int = 1

    var adapterItemClicked: ((ArrayList<AppData>) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == SELECTED)
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.app_view_selected, null)
            ) else
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.app_view, null)
            )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    private fun sendSelectedApp() {
        selectedAppList.clear()
        for (i in appList.indices) {
            if (appList[i].selected) selectedAppList.add(appList[i])
        }
        adapterItemClicked?.invoke(selectedAppList)
    }

    var getSelectedAppList = selectedAppList

    fun setAllChecked(b: Boolean) {
        selectedAppList.clear()
        appList.map {
            it.selected = b
        }
        selectedAppList.addAll(appList)
        notifyDataSetChanged()
        if (!b) selectedAppList.clear()
        adapterItemClicked?.invoke(selectedAppList)
    }

    fun setAppList(appList: ArrayList<AppData>) {
        this.appList = appList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (appList[position].selected) SELECTED else NORMAL
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appNameView: MaterialTextView = itemView.findViewById(R.id.appName)
        private val appSizeView: MaterialTextView = itemView.findViewById(R.id.appSize)
        private val appIconView: ShapeableImageView = itemView.findViewById(R.id.appIconView)

        init {
            itemView.setOnClickListener {
                appList[adapterPosition].apply {
                    this.selected = !this.selected
                }.also {
                    notifyDataSetChanged()
                    sendSelectedApp()
                }
            }
        }

        fun bind(appData: AppData) {
            appNameView.text = appData.appName
            appSizeView.text = appData.apkSize
            appIconView.setImageDrawable(appData.appIcon)
        }

    }

}