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

//    val rewardCards: List<RewardCard> = listOf(RewardCard(cardId=11, points=1, restaurantLevel=5, restaurantName="喜憨兒ENJOY 臺北餐廳"))

    private val _rewardCards = MutableLiveData<List<RewardCard>>()
    val rewardCards: LiveData<List<RewardCard>>
        get() = _rewardCards

    val _currentCardLevel = MutableLiveData<Int>()
    val currentCardLevel: LiveData<Int>
        get() = _currentCardLevel

    val _currentLevelImage = MutableLiveData<Int>()
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
//                for (i in 0 until rewardCards.value!!.size) {
//                    _currentCardLevel.value = rewardCards.value!![i].restaurantLevel
//                    setRewardCardLevelImage()
//                }
            } else {
                i(TAG, "該名使用者還沒有集點卡喔")
            }
        })
    }

//    private fun setRewardCardLevelImage() {
//
//        for (i in 0 until rewardCards.value!!.size) {
//
//            when (rewardCards.value!![i].restaurantLevel) {
//                1 -> _currentLevelImage.value = R.drawable.icons8_slug_eating_80
//                2 -> _currentLevelImage.value = R.drawable.plant_2
//                3 -> _currentLevelImage.value = R.drawable.icons8_sprout_100
//                4 -> _currentLevelImage.value = R.drawable.icons8_oak_tree_100
//                5 -> _currentLevelImage.value = R.drawable.icons8_treehouse_100
//            }
//            i(TAG, "currentCardLevel.value = ${currentCardLevel.value}")
//        }
//    }
}