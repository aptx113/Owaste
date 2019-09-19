package com.epoch.owaste

import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.card.RewardCardAdapter
import com.epoch.owaste.data.RewardCard


fun bindRecyclerViewWithRewardCards(recyclerView: RecyclerView, rewardCards: List<RewardCard>?) {
    rewardCards?.let {
        recyclerView.adapter?.apply {


        }
    }
}