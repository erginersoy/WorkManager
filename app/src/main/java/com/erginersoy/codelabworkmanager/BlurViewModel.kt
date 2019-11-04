package com.erginersoy.codelabworkmanager

import android.app.Application
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.erginersoy.codelabworkmanager.worker.BlurWorker
import com.erginersoy.codelabworkmanager.worker.CleanupWorker
import com.erginersoy.codelabworkmanager.worker.SaveImageToFileWorker


/**
 * Created by ErginErsoy on
 * 2019-11-04.
 */
class BlurViewModel(application: Application) : AndroidViewModel(application) {
    private var mWorkManager: WorkManager
    private var mImageUri: Uri? = null
    private var mOutputUri: Uri? = null
    private var mSavedWorkInfo: LiveData<List<WorkInfo>>

    init {
        mWorkManager = WorkManager.getInstance(application)
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    fun applyBlur(blurLevel: Int) {
        var continuation = mWorkManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )
        for (i in 0..blurLevel) {

            val blurRequest = OneTimeWorkRequest.Builder(BlurWorker::class.java)
            if (i == 0) {
                blurRequest.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurRequest.build());
            // Add WorkRequest to save the image to the filesystem
        }
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val save = OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java)
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)

        // Actually start the work
        continuation.enqueue()
    }

    private fun uriOrNull(uriString: String): Uri? {
        return if (!TextUtils.isEmpty(uriString)) {
            Uri.parse(uriString)
        } else null
    }

    /**
     * Setters
     */
    fun setImageUri(uri: String) {
        mImageUri = uriOrNull(uri)
    }

    /**
     * Getters
     */
    fun getImageUri(): Uri? {
        return mImageUri
    }

    fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        mImageUri?.let {
            builder.putString(KEY_IMAGE_URI, it.toString())
        }
        return builder.build()
    }

    fun getOutputWorkInfo(): LiveData<List<WorkInfo>> {
        return mSavedWorkInfo
    }

    fun setOutputUri(outputImageUri: String) {
        mOutputUri = uriOrNull(outputImageUri)
    }

    fun getOutputUri(): Uri? {
        return mOutputUri
    }

    fun cancelWork() {
        mWorkManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }
}