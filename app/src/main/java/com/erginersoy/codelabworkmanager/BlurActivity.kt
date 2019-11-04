package com.erginersoy.codelabworkmanager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_blur.*

class BlurActivity : AppCompatActivity() {

    private lateinit var viewModel: BlurViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        viewModel = ViewModelProviders.of(this).get(BlurViewModel::class.java)
        val intent = intent
        val imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI)
        viewModel.setImageUri(imageUriExtra)
        if (viewModel.getImageUri() != null) {
            Glide.with(this).load(viewModel.getImageUri()).into(image_view)
        }

        go_button.setOnClickListener { viewModel.applyBlur(getBlurLevel()) }
        viewModel.getOutputWorkInfo().observe(this, Observer {
            if (it == null || it.isEmpty()) {
                return@Observer
            }
            val workInfo = it.get(0)
            val finished = workInfo.state.isFinished
            if (!finished) {
                showWorkInProgress()
            } else {
                showWorkFinished()
                val outputData = workInfo.outputData

                val outputImageUri = outputData.getString(KEY_IMAGE_URI)
                if (!TextUtils.isEmpty(outputImageUri)) {
                    viewModel.setOutputUri((outputImageUri!!))
                    see_file_button.visibility = View.VISIBLE
                }
            }
        })
        see_file_button.setOnClickListener {
            val currentUri = viewModel.getOutputUri()
            if (currentUri != null) {
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                if (actionView.resolveActivity(packageManager) != null) {
                    startActivity(actionView)
                }
            }
        }
        cancel_button.setOnClickListener { viewModel.cancelWork() }

    }

    private fun getBlurLevel(): Int {
        when (radio_blur_group.getCheckedRadioButtonId()) {
            R.id.radio_blur_lv_1 -> return 1
            R.id.radio_blur_lv_2 -> return 2
            R.id.radio_blur_lv_3 -> return 3
        }

        return 1
    }

    private fun showWorkInProgress() {
        progress_bar.visibility = View.VISIBLE
        cancel_button.visibility = View.VISIBLE
        go_button.visibility = View.GONE
        see_file_button.visibility = View.GONE
    }

    private fun showWorkFinished() {
        progress_bar.visibility = View.GONE
        cancel_button.visibility = View.GONE
        go_button.visibility = View.VISIBLE
    }

}
