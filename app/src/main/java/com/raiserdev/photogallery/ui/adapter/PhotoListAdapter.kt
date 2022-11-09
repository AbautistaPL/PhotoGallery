package com.raiserdev.photogallery.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.raiserdev.photogallery.R
import com.raiserdev.photogallery.databinding.ListItemGalleryBinding
import com.raiserdev.photogallery.model.GalleryItem

class PhotoViewHolder(
    private val binding: ListItemGalleryBinding
): RecyclerView.ViewHolder(binding.root){
    fun bind(galleryItem: GalleryItem, onItemClicked: (Uri) -> Unit){
        binding.itemImageView.load(galleryItem.url){
            placeholder(R.drawable.ic_cloud_load)
        }
        binding.root.setOnClickListener { onItemClicked(galleryItem.photoPageUri) }
    }
}

class PhotoListAdapter (
    private val galleryItems : List<GalleryItem>,
    private val onItemClicked: (Uri) -> Unit
): RecyclerView.Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = galleryItems[position]
        holder.bind(item, onItemClicked = onItemClicked)
    }

    override fun getItemCount(): Int = galleryItems.size

}

