package com.epoch.owaste.card

import android.util.Log.i
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.R
import com.epoch.owaste.data.OwasteRepository
import com.epoch.owaste.data.RewardCard
import com.google.android.gms.tasks.OnSuccessListener

class RewardCardViewModel : ViewModel() {

    val TAG = "Eltin_" + this.javaClass.simpleName

    private val _rewardCards = MutableLiveData<List<RewardCard>>()
    val rewardCards: LiveData<List<RewardCard>>
        get() = _rewardCards

    init {
        getAllRewardCardsFromFirestore()
    }

    private fun getAllRewardCardsFromFirestore() {

        OwasteRepository.getAllRewardCardsFromFirestore(OnSuccessListener {

            if (!it.isEmpty) {

                val allRewardCardsResult = it.toObjects(RewardCard::class.java)
                _rewardCards.value = allRewardCardsResult
                i(TAG, "rewardCards.value = ${rewardCards.value}")
            } else {
                i(TAG, "該名使用者還沒有集點卡喔")
            }
        })
    }
}