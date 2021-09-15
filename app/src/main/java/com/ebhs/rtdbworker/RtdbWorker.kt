package com.ebhs.rtdbworker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class RtdbWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    companion object {
        const val TAG = "RtdbWorker"
        const val WorkerKey = "WorkerKey"

        //        const val docPath = "/doc/id123"
        const val docPath = "/doc/notExist"

        suspend fun tryGetData() = coroutineScope {
            Log.d(TAG, "Getting data")
            return@coroutineScope getData().also {
                Log.d(TAG, "${it}")
            }
        }

        fun scheduleWorker(context: Context) {
            Log.d(TAG, "Enqueuing worker")

            val worker = PeriodicWorkRequestBuilder<RtdbWorker>(30, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WorkerKey, ExistingPeriodicWorkPolicy.KEEP, worker)
        }

        fun stopWorker(context: Context) {
            Log.d(TAG, "Stop worker")
            WorkManager.getInstance(context).cancelUniqueWork(WorkerKey)
        }

        private suspend fun getData() = withContext(Dispatchers.IO) {
            Rtdb.instance.getReference(docPath)
                .get()
                .await()
                .getValue<Map<String, Any>>()
        }
    }

    override suspend fun doWork(): Result {
        val data = try {
            getData()
        } catch (e: Exception) {
            val errMsg = "Error message"
            Log.e(TAG, errMsg, e)
            return Result.retry()
        }

        Log.d(TAG, "${data}")
        return Result.success()
    }
}

object Rtdb {
    private val defaultInstance: FirebaseDatabase = Firebase.database

    init {
        defaultInstance.setPersistenceEnabled(true)
        defaultInstance.setLogLevel(Logger.Level.DEBUG)
    }

    @JvmStatic
    val instance: FirebaseDatabase
        get() = this.defaultInstance
}
