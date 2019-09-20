package com.epoch.owaste.card

import android.content.res.Resources
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

//    val rewardCards: List<RewardCard> = listOf(RewardCard(cardId=11, points=1, restaurantLevel=5, restaurantName="喜憨兒ENJOY 臺北餐廳"))

    private val _rewardCards = MutableLiveData<List<RewardCard>>()
    val rewardCards: LiveData<List<RewardCard>>
        get() = _rewardCards

    val _currentQRCodeLevel = MutableLiveData<String>()
    val currentQRCodeLevel: LiveData<String>
        get() = _currentQRCodeLevel

    private val _currentLevelImage = MutableLiveData<Int>()
    val currentLevelImage: LiveData<Int>
        get() = _currentLevelImage

    init {
        getAllRewardCardsFromFirestore()
    }

    private fun getAllRewardCardsFromFirestore() {

        OwasteRepository.getAllRewardCardsFromFirestore(OnSuccessListener {
            if (!it.isEmpty) {

                val allRewardCardsResult = it.toObjects(RewardCard::class.java)
                _rewardCards.value = allRewardCardsResult
                i(TAG, "rewardCards.value = ${rewardCards.value}")
                setRewardCardLevelImage()
            } else {
                i(TAG, "該名使用者還沒有集點卡喔")
            }
        })
    }

    fun setRewardCardLevelImage() {

        when (currentQRCodeLevel.value) {
            "1" -> _currentLevelImage.value = R.drawable.icons8_slug_eating_80
            "2" -> _currentLevelImage.value = R.drawable.plant_2
            "3" -> _currentLevelImage.value = R.drawable.icons8_sprout_100
            "4" -> _currentLevelImage.value = R.drawable.icons8_oak_tree_100
            "5" -> _currentLevelImage.value = R.drawable.icons8_treehouse_100
        }

    }
}