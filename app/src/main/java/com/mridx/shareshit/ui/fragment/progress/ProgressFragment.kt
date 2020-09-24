package com.mridx.shareshit.ui.fragment.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.mridx.shareshit.R
import com.mridx.shareshit.viewmodel.MainUIViewModel
import kotlinx.android.synthetic.main.progress_fragment.*

class ProgressFragment(private val viewModel: MainUIViewModel) : Fragment() {


    companion object {
        fun newInstance(viewModel: MainUIViewModel) = ProgressFragment(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.progress_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.progress.observe(viewLifecycleOwner, {
            appProgress?.max = (it.size / 1024).toInt()
            appProgress?.progress = ((it.progress / 1024).toInt())
            appCurrent?.text = "Receiving ${it.name}"
        })


    }

}