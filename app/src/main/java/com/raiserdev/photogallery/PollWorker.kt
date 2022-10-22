package com.raiserdev.photogallery

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.raiserdev.photogallery.model.PhotoRepository
import kotlinx.coroutines.flow.first

private const val TAG = "PollWorker"
class PollWorker (
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters){
    override suspend fun doWork(): Result {
        Log.i(TAG, "Work request triggered.")
        val preferencesRepository = PreferencesRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferencesRepository.storeQuery.first()
        val lastId = preferencesRepository.lastResultId.first()
        if (query.isEmpty()){
            Log.i(TAG,"No saved query, finished early.")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query = query)

            if (items.isNotEmpty()){
                val newResultId = items.first().id
                if (newResultId == lastId){
                    Log.i(TAG, "Still have the same result: $newResultId")
                }else{
                    Log.i(TAG,"Got a new result: $newResultId")
                    preferencesRepository.setLastResultId(newResultId)
                }
            }
            Result.success()

        }catch (ex: Exception){
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
        //return Result.success()
    }

}