package com.epoch.owaste.maps

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.data.Photo
import com.epoch.owaste.data.RewardCard
import com.epoch.owaste.databinding.ItemPlaceDetailsPhotoBinding

class PlaceDetailsPhotoAdapter :
    ListAdapter<Photo, PlaceDetailsPhotoAdapter.PhotoViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class PhotoViewHolder(private var binding: ItemPlaceDetailsPhotoBinding):
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.photo_reference == newItem.photo_reference
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }

    }
}