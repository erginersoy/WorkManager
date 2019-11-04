package com.erginersoy.codelabworkmanager

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_select_image.*
import java.util.*


class SelectImageActivity : AppCompatActivity() {

    private val TAG = "SelectImageActivity"

    private val REQUEST_CODE_IMAGE = 100
    private val REQUEST_CODE_PERMISSIONS = 101

    private val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
    private val MAX_NUMBER_REQUEST_PERMISSIONS = 2

    private val sPermissions = Arrays.asList(
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    )
    private var mPermissionRequestCount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)
        savedInstanceState?.let {
            mPermissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0);
        }
        requestPermissionsIfNecessary();

        selectImage.setOnClickListener {
            val chooseIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, mPermissionRequestCount)
    }

    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    this,
                    sPermissions.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                Toast.makeText(
                    this, R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
                selectImage.setEnabled(false)
            }
        }
    }

    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in sPermissions) {
            hasPermissions = hasPermissions and (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED)
        }
        return hasPermissions
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary() // no-op if permissions are granted already.
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> handleImageRequestResult(data)
                else -> Log.d(TAG, "Unknown request code.")
            }
        } else {
            Log.e(TAG, String.format("Unexpected Result code %s", resultCode))
        }
    }

    private fun handleImageRequestResult(data: Intent?) {
        var imageUri: Uri? = null
        if (data?.clipData != null) {
            imageUri = data.clipData?.getItemAt(0)?.uri
        } else {
            imageUri = data?.data
        }
        imageUri?.let {
            val filterIntent = Intent(this, BlurActivity::class.java)
            filterIntent.putExtra(KEY_IMAGE_URI, it.toString());
            startActivity(filterIntent)

        }
    }

}
