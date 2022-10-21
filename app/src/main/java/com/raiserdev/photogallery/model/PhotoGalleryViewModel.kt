package com.raiserdev.photogallery.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raiserdev.photogallery.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"
class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository = PhotoRepository()
    private val preferencesRepository = PreferencesRepository.get()

    private val _galleryItems: MutableStateFlow<List<GalleryItem>> =
        MutableStateFlow(emptyList())

    val galleryItems: StateFlow<List<GalleryItem>>
        get() = _galleryItems.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.storeQuery.collectLatest { storedQuery ->
                try {
                    //val items = photoRepository.searchPhotos("llama")
                    val items = fetchGalleryItems(storedQuery)
                    Log.d(TAG, "Items received: $items")
                    _galleryItems.value = items
                }catch (ex: Exception){
                    Log.e(TAG, "Failed to fetch gallery items ", ex)
                }
            }

        }
    }

    fun setQuery(query:String){

        viewModelScope.launch {
            //_galleryItems.value = fetchGalleryItems(query)
            preferencesRepository.setStoredQuery(query)
        }
    }

    private suspend fun fetchGalleryItems(query: String):List<GalleryItem>{
        return if (query.isNotEmpty()){
            photoRepository.searchPhotos(query)
        }else{
            photoRepository.fetchPhotos()
        }
    }
}