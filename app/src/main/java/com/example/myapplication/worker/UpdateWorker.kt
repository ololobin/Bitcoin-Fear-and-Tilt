package com.example.myapplication.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.data.repository.CryptoRepository
import com.example.myapplication.widget.BtcWidget

class UpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "UpdateWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "UpdateWorker starting background refresh...")
        val repository = CryptoRepository(applicationContext)
        val result = repository.refreshData()
        
        return if (result.isSuccess) {
            Log.d(TAG, "UpdateWorker successfully refreshed data.")
            // Trigger Glance widget update
            try {
                BtcWidget().updateAll(applicationContext)
                Log.d(TAG, "UpdateWorker triggered Glance Widget updateAll.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update Glance widget: ${e.message}", e)
            }
            Result.success()
        } else {
            Log.w(TAG, "UpdateWorker failed: ${result.exceptionOrNull()?.message}")
            Result.retry()
        }
    }
}
