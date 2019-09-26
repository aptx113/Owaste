package com.epoch.owaste

import android.util.Log.i
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.card.RewardCardAdapter
import com.epoch.owaste.data.Photo
import com.epoch.owaste.data.RewardCard
import com.epoch.owaste.maps.PlaceDetailsPhotoAdapter

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
    photos?.let {
        i("Eltin", "bindRecyclerViewWithPhotos.List<Photo> = $it")
        recyclerView.adapter?.apply {
            when (this) {
                is PlaceDetailsPhotoAdapter -> {
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

//@BindingAdapter("levelicon")
//fun bindRewardCardLevelImageWithLevel(imageView: ImageView, currentLevelImage: Int) {
//    imageView.setImageResource(currentLevelImage)
//}