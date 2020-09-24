package com.mridx.shareshit.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mridx.shareshit.R
import com.mridx.shareshit.adapter.ViewPagerAdapter
import com.mridx.shareshit.ui.fragment.app.AppFragment
import com.mridx.shareshit.ui.fragment.progress.ProgressFragment
import com.mridx.shareshit.util.Utils
import com.mridx.shareshit.util.Utils.Companion.MB
import com.mridx.shareshit.viewmodel.MainUIViewModel
import kotlinx.android.synthetic.main.main_ui.*

class MainUI : AppCompatActivity() {

    lateinit var userType: Utils.TYPE
    lateinit var viewModel: MainUIViewModel
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_ui)

        if (intent.extras == null) {
            finish()
            return
        }
        userType = (intent.extras!!["TYPE"] as Utils.TYPE?)!!

        viewModel = ViewModelProvider(this).get(MainUIViewModel::class.java)

        viewModel.setup(userType)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewModel.totalSent.observe(this, {
            //Toast.makeText(this, "${it / MB}", Toast.LENGTH_SHORT).show()
        })

        viewModel.transferSuccess.observe(this, {
            if (it)
                Toast.makeText(this, "Transfer complete", Toast.LENGTH_LONG).show()
        })

        viewModel.progress.observe(this, {
            Log.d(
                "kaku",
                "onCreate: downloading ${it.name}  ${(it.progress / MB).toDouble()} of ${(it.size / MB).toDouble()}"
            )
        })

        setupViewPager()


    }

    private fun setupViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        val appFragment = AppFragment.newInstance().also {
            it.onSendAction = viewModel::onSendAction
        }

        viewPagerAdapter.addFragment(appFragment, "App")
        viewPagerAdapter.addFragment(ProgressFragment.newInstance(viewModel), "Progress")
        viewPager?.adapter = viewPagerAdapter
    }

}