package com.mridx.shareshit.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mridx.shareshit.R
import kotlinx.android.synthetic.main.start_ui.*

class StartUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_ui)

        joinBtn?.setOnClickListener { actionJoin() }
        createBtn?.setOnClickListener { actionCreate() }

    }

    private fun actionCreate() {
        startActivity(Intent(this, CreateUI::class.java))
    }

    private fun actionJoin() {
        startActivity(Intent(this, JoinUI::class.java))
    }

}