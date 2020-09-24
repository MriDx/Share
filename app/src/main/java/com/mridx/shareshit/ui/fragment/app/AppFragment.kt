package com.mridx.shareshit.ui.fragment.app

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mridx.shareshit.R
import com.mridx.shareshit.adapter.AppAdapter
import com.mridx.shareshit.data.AppData
import com.mridx.shareshit.util.Utils
import kotlinx.android.synthetic.main.app_fragment.*

class AppFragment : Fragment() {


    var onSendAction: ((ArrayList<Any>, Utils.FileSenderType) -> Unit)? = null

    companion object {
        fun newInstance() = AppFragment()
    }

    private lateinit var viewModel: AppViewModel
    private lateinit var appAdapter: AppAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        appAdapter = AppAdapter().also {
            it.adapterItemClicked = this::onAdapterItemClicked
        }

        appsHolder.apply {
            adapter = appAdapter
            layoutManager = GridLayoutManager(context, 4)
        }

        viewModel.getApps(requireContext())

        appCheckbox?.setOnCheckedChangeListener { _, b -> appAdapter.setAllChecked(b) }

        viewModel.appList.observe(viewLifecycleOwner, {
            appAdapter.apply {
                setAppList(it)
            }.also {
                progressBar?.visibility = View.GONE
            }
        })

        appSendBtn?.setOnClickListener {
            val selectedList: ArrayList<AppData> = appAdapter.getSelectedAppList
            if (selectedList.size == 0) {
                Toast.makeText(context, "Select at least one App", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(
                context,
                "Send selected apps, Total - " + selectedList.size,
                Toast.LENGTH_SHORT
            ).show()
            onSendAction?.invoke(selectedList as ArrayList<Any>, Utils.FileSenderType.APP)
        }

    }

    private fun showSendBtn(size: Int) {
        when {
            size > 0 -> {
                btmView.visibility = View.VISIBLE
                appSendBtn.text = "Send ($size)"
            }
            else -> btmView.visibility = View.GONE
        }
    }

    private fun onAdapterItemClicked(list: ArrayList<AppData>) {
        showSendBtn(list.size)
    }

}