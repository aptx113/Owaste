package com.epoch.owaste.card

import android.util.Log.i
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.data.OwasteRepository
import com.epoch.owaste.data.RewardCard

class RewardCardViewModel : ViewModel() {

    val TAG = "Eltin_" + this.javaClass.simpleName

    val _rewardCards = MutableLiveData<List<RewardCard>>()
    val rewardCards: LiveData<List<RewardCard>>
        get() = _rewardCards

    val _currentQRCodeLevel = MutableLiveData<String>()
    val currentQRCodeLevel: LiveData<String>
        get() = _currentQRCodeLevel

    init {
        OwasteRepository.getAllRewardCardFromFirestore()
        _rewardCards.value = OwasteRepository._allRewardCards.value
        i(TAG, "LiveData<List<RewardCard>> value = ${rewardCards.value}")
    }

    fun setRewardCardLevelImage() {

    }
}