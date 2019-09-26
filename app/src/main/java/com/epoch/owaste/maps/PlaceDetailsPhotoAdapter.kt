package com.epoch.owaste.maps

import android.util.Log.i
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epoch.owaste.BuildConfig
import com.epoch.owaste.OwasteApiService
import com.epoch.owaste.data.Photo
import com.epoch.owaste.data.RewardCard
import com.epoch.owaste.databinding.ItemPlaceDetailsPhotoBinding
import kotlinx.android.synthetic.main.item_place_details_photo.view.*

private const val HOST_NAME = "maps.googleapis.com/maps"
private const val BASE_URL = "https://$HOST_NAME/api/"
private const val END_POINT = "place/photo?"
private const val MAX_HEIGHT = "maxheight=1600"
private const val PHOTO_REF = "&photoreference="
private const val KEY = "&key="
private const val TAG = "Eltin_PlaceDetailsPhotoAdapter"

class PlaceDetailsPhotoAdapter :
    ListAdapter<Photo, PlaceDetailsPhotoAdapter.PhotoViewHolder>(DiffCallback) {

    val photoRefToUrl = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        return PhotoViewHolder(ItemPlaceDetailsPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        val photo = getItem(position)

        i(TAG, "photos.photo_reference = ${photo.photo_reference}")

        if (photoRefToUrl.containsKey(photo.photo_reference)) {

            i(TAG, "photoRefToUrl = ${photoRefToUrl[photo.photo_reference]}")

            Glide.with(holder.itemView.context)
                .load(photoRefToUrl[photo.photo_reference])
                .into(holder.itemView.img_place_details_photo)
        } else {
            photoRefToUrl[photo.photo_reference] =
                BASE_URL + END_POINT + MAX_HEIGHT + PHOTO_REF +
                        photo.photo_reference + KEY + BuildConfig.API_KEY
            i(TAG, "put photoRefToUrl = ${photoRefToUrl[photo.photo_reference]}")
            Glide.with(holder.itemView.context)
                .load(photoRefToUrl[photo.photo_reference])
                .into(holder.itemView.img_place_details_photo)
        }
        holder.bind(photo)
    }

    class PhotoViewHolder(private var binding: ItemPlaceDetailsPhotoBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {

            binding.photo = photo

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
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