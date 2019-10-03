package com.epoch.owaste.maps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.data.PlaceReviews
import com.epoch.owaste.databinding.ItemPlaceReviewBinding

class PlaceDetailsReviewsAdapter :
    ListAdapter<PlaceReviews, PlaceDetailsReviewsAdapter.ReviewViewHolder>(DiffCallback){

    class ReviewViewHolder(private var binding: ItemPlaceReviewBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: PlaceReviews) {

            binding.review = review

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlaceReviews>() {
        override fun areItemsTheSame(oldItem: PlaceReviews, newItem: PlaceReviews): Boolean {
            return oldItem.author_url == newItem.author_url
        }

        override fun areContentsTheSame(oldItem: PlaceReviews, newItem: PlaceReviews): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {

        return ReviewViewHolder((ItemPlaceReviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)))
    }

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {

        val review = getItem(position)
        holder.bind(review)
    }
}