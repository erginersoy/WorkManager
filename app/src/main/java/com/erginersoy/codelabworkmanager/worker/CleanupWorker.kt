package com.erginersoy.codelabworkmanager.worker

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.erginersoy.codelabworkmanager.OUTPUT_PATH
import java.io.File


/**
 * Created by ErginErsoy on
 * 2019-11-04.
 */
class CleanupWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val TAG = CleanupWorker::class.java.simpleName

    override fun doWork(): Result {

        try {
            WorkerUtils.makeStatusNotification("Doing <CleanupWorker>", applicationContext);
            WorkerUtils.sleep()
            val outputDirectory = File(
                applicationContext.filesDir,
                OUTPUT_PATH
            )
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries.size > 0) {
                    for (entry in entries) {
                        val name = entry.name
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i(TAG, String.format("Deleted %s - %s", name, deleted))
                        }
                    }
                }
            }
            return Result.success();
        } catch (exception: Exception) {
            Log.e(TAG, "Error cleaning up", exception);
            return Result.failure();
        }
    }
}