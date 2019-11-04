package com.erginersoy.codelabworkmanager.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.erginersoy.codelabworkmanager.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by ErginErsoy on
 * 2019-11-04.
 */
class SaveImageToFileWorker(val context: Context, val workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val TAG = SaveImageToFileWorker::class.java.simpleName
    private val TITLE = "Blurred Image"
    private val DATE_FORMATTER = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())


    override fun doWork(): Result {
        try {
            WorkerUtils.makeStatusNotification("Doing <SaveImageToFileWorker>", applicationContext);
            WorkerUtils.sleep()

            val resolver = applicationContext.contentResolver
            val resourceUri = workerParams.inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))
            val outputUri = MediaStore.Images.Media.insertImage(resolver, bitmap, TITLE, DATE_FORMATTER.format(Date()))


            if (TextUtils.isEmpty(outputUri)) {
                Log.e(TAG, "Writing to MediaStore failed")
                return Result.failure()
            }
            val outputData = Data.Builder()
                .putString(KEY_IMAGE_URI, outputUri)
                .build()
            return Result.success(outputData)
        } catch (exception: Exception) {
            return Result.failure()
        }
    }
}