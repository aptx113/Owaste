package com.epoch.owaste

import android.util.Log.i
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.epoch.owaste.card.RewardCardAdapter
import com.epoch.owaste.data.Photo
import com.epoch.owaste.data.PlaceReviews
import com.epoch.owaste.data.RewardCard
import com.epoch.owaste.maps.PlaceDetailsPhotoAdapter
import com.epoch.owaste.maps.PlaceDetailsReviewsAdapter

@BindingAdapter("rewardcards")
fun bindRecyclerViewWithRewardCards(recyclerView: RecyclerView, rewardCards: List<RewardCard>?) {
    rewardCards?.let {
        i("Eltin", "bindRecyclerViewWithRewardCards.List<RewardCard> = $it")
        recyclerView.adapter?.apply {
            when (this) {
                is RewardCardAdapter -> {
                    when (itemCount) {
                        0 -> submitList(it)
                        it.size -> notifyDataSetChanged()
                        else -> submitList(it)
                    }
                }
            }

        }
    }
}

@BindingAdapter("photos")
fun bindRecyclerViewWithPhotos(recyclerView: RecyclerView, photos: List<Photo>?) {
    i("Eltin", "bindRecyclerViewWithPhotos.List<Photo> = $photos")
    photos?.let {
        i("Eltin", "bindRecyclerViewWithPhotos.List<Photo> = $it")
        recyclerView.adapter?.apply {
            when (this) {
                is PlaceDetailsPhotoAdapter -> submitList(it)
//                {
//                    when (itemCount) {
//                        0 -> submitList(it)
//                        it.size -> notifyDataSetChanged()
//                        else -> submitList(it)
//                    }
//                }
            }
        }
    }
}

@BindingAdapter("reviews")
fun bindRecyclerViewWithPlaceReviews(recyclerView: RecyclerView, rewardCards: List<PlaceReviews>?) {
    rewardCards?.let {
        i("Eltin", "bindRecyclerViewWithPlaceReviews.List<PlaceReviews> = $it")
        recyclerView.adapter?.apply {
            when (this) {
                is PlaceDetailsReviewsAdapter -> submitList(it)
            }

        }
    }
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    i("Eltin", "imageUrl = $imgUrl")
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder))
            .into(imgView)
    }
}