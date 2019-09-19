package com.epoch.owaste

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.card.RewardCardAdapter
import com.epoch.owaste.data.RewardCard

@BindingAdapter("rewardcards")
fun bindRecyclerViewWithRewardCards(recyclerView: RecyclerView, rewardCards: List<RewardCard>?) {
    rewardCards?.let {
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