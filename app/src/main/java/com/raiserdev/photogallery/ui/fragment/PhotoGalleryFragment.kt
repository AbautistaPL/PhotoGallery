package com.raiserdev.photogallery.ui.fragment

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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.raiserdev.photogallery.PollWorker
import com.raiserdev.photogallery.R
import com.raiserdev.photogallery.databinding.FragmentPhotoGalleryBinding
import com.raiserdev.photogallery.model.PhotoGalleryViewModel
import com.raiserdev.photogallery.ui.adapter.PhotoListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment : Fragment(),MenuProvider {
    private var _binding: FragmentPhotoGalleryBinding?= null
    private val binding get() = checkNotNull(_binding)
    {
        "Cannot access binding because it is null. Is the view visible?"
    }

    private var searchView: SearchView?= null
    private val photoGalleryViewModel : PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest = OneTimeWorkRequest
            .Builder(PollWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueue(workRequest)
    }

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
                    binding.photoGrid.adapter = PhotoListAdapter(state.images)
                    searchView?.setQuery(state.query,false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_photo_gallery,menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as? SearchView

        val clearSearch: MenuItem = menu.findItem(R.id.menu_item_clear)

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
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

}

