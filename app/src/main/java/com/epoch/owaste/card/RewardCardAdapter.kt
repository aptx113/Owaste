package com.epoch.owaste.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epoch.owaste.data.RewardCard
import com.epoch.owaste.databinding.ItemRewardCardBinding

class RewardCardAdapter(val viewModel: RewardCardViewModel) :
    ListAdapter<RewardCard, RewardCardAdapter.RewardCardViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardCardViewHolder {

        return RewardCardViewHolder(ItemRewardCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RewardCardViewHolder, position: Int) {

        val rewardCard = getItem(position)

        holder.bind(rewardCard, viewModel)
    }

    class RewardCardViewHolder(private var binding: ItemRewardCardBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rewardCard: RewardCard, viewModel: RewardCardViewModel) {
            binding.rewardCard = rewardCard
            binding.viewModel = viewModel
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<RewardCard>() {
        override fun areItemsTheSame(oldItem: RewardCard, newItem: RewardCard): Boolean {
            return oldItem.cardId == newItem.cardId
        }

        override fun areContentsTheSame(oldItem: RewardCard, newItem: RewardCard): Boolean {
            return oldItem == newItem
        }

    }


}