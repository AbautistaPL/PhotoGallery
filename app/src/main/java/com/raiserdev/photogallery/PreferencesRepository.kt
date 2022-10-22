package com.raiserdev.photogallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object{
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")

        private var INSTANCE: PreferencesRepository ?= null
        private const val SETTINGS = "settings"

        fun initialize(context: Context){
            if (INSTANCE == null){
                val dataStore = PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(SETTINGS) }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get() : PreferencesRepository{
            return INSTANCE ?: throw java.lang.IllegalStateException(
                "PreferencesRepository  must be initialized"
            )
        }
    }

    val storeQuery : Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String){
        dataStore.edit{
            it[SEARCH_QUERY_KEY] = query
        }
    }
    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastResultId(lastResultId: String){
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }
}