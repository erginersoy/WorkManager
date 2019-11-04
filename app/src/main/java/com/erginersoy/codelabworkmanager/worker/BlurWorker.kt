package com.erginersoy.codelabworkmanager.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.erginersoy.codelabworkmanager.KEY_IMAGE_URI


/**
 * Created by ErginErsoy on
 * 2019-11-04.
 */
class BlurWorker(context: Context, val workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val TAG = BlurWorker::class.java.simpleName
    override fun doWork(): Result {
        val resourceUri = workerParams.inputData.getString(KEY_IMAGE_URI)

        try {
            WorkerUtils.makeStatusNotification("Doing <BlurWorker>", applicationContext);
            WorkerUtils.sleep()
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val resolver = applicationContext.contentResolver
            val picture =
                BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            val blurImage = WorkerUtils.blurBitmap(picture, applicationContext)
            val outputUri = WorkerUtils.writeBitmapToFile(applicationContext, blurImage)
            val outputData = Data.Builder()
                .putString(KEY_IMAGE_URI, outputUri.toString())
                .build()
            return Result.success(outputData)
        } catch (t: Throwable) {
            Log.e(TAG, "Error applying blur", t)
            return Result.failure()
        }
    }
}