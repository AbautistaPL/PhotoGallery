package com.raiserdev.photogallery.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.raiserdev.photogallery.PollWorker
import com.raiserdev.photogallery.R
import com.raiserdev.photogallery.databinding.FragmentPhotoGalleryBinding
import com.raiserdev.photogallery.model.PhotoGalleryViewModel
import com.raiserdev.photogallery.ui.adapter.PhotoListAdapter
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val POLL_WORK = "poll_work"

private const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment : Fragment(),MenuProvider {
    private var _binding: FragmentPhotoGalleryBinding?= null
    private val binding get() = checkNotNull(_binding)
    {
        "Cannot access binding because it is null. Is the view visible?"
    }

    private var searchView: SearchView?= null
    private var pollingMenuItem: MenuItem?= null
    private val photoGalleryViewModel : PhotoGalleryViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater,container,false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                /*photoGalleryViewModel.galleryItems.collect{ items ->
                    Log.d(TAG, "Response received : $items")
                    binding.photoGrid.adapter = PhotoListAdapter(items)
                }*/
                photoGalleryViewModel.uiState.collect{ state ->
                    binding.photoGrid.adapter = PhotoListAdapter(
                        state.images
                    ){
                        photoPageUri ->
                        val intent = Intent(Intent.ACTION_VIEW,photoPageUri)
                        startActivity(intent)
                    }
                    searchView?.setQuery(state.query,false)
                    updatePollingState(state.isPolling)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView = null
        pollingMenuItem = null
    }

    private fun updatePollingState(isPolling : Boolean){
        val toggleItem = if(isPolling){
            R.string.stop_polling
        }else{
            R.string.start_polling
        }
        pollingMenuItem?.setTitle(toggleItem)
        if (isPolling){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            val periodicRequest =
                PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                POLL_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }else
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_photo_gallery,menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as? SearchView

        val clearSearch: MenuItem = menu.findItem(R.id.menu_item_clear)
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_polling)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(query: String?): Boolean {
                photoGalleryViewModel.setQuery(query ?: "")
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "QueryTextChange: $query")
                return false
            }
        })

        clearSearch.setOnMenuItemClickListener {
            searchView?.setQuery("",false)
            true
        }

        pollingMenuItem?.setOnMenuItemClickListener {
            photoGalleryViewModel.toggleIsPolling()
            true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

}

