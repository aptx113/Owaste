package com.epoch.owaste.data

import android.util.Log.i
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.epoch.owaste.Owaste
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object OwasteRepository {

    private const val TAG = "Eltin_OwasteR"
    private const val CARD_ID = "cardId"
    private const val REWARD_CARD = "rewardcards"

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    val _currentQRCodeLevel = MutableLiveData<String>()
    val currentQRCodeLevel: LiveData<String>
        get() = _currentQRCodeLevel

    val _currentQRCodeCardId = MutableLiveData<String>()
    val currentQRCodeCardId: LiveData<String>
        get() = _currentQRCodeCardId

    val _currentQRCodeRestaurantName = MutableLiveData<String>()
    val currentQRCodeRestaurantName: LiveData<String>
        get() = _currentQRCodeRestaurantName

    val _allRewardCards = MutableLiveData<List<RewardCard>>()
    val allRewardCards: LiveData<List<RewardCard>>
        get() = _allRewardCards

//    val _currentLevelImage = MutableLiveData<Int>()
//    val currentLevelImage: LiveData<Int>
//        get() = _currentLevelImage

    fun initCurrentUserIfFirstTime (onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(
                    exp = 0,
                    level = 1,
                    uid = FirebaseAuth.getInstance().currentUser!!.uid
                )
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                    i(TAG,"currentUserDocRef = ${currentUserDocRef.id}")
                }
            }
            else
                onComplete()
        }
    }
    fun onQRCodeScannedUpdateCard() {
        currentUserDocRef.collection(REWARD_CARD)
            .whereEqualTo(CARD_ID, currentQRCodeCardId.value?.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val newRewardCard = RewardCard(
                        cardId = currentQRCodeCardId.value!!.toLong(),
                        points = 1,
                        restaurantLevel = currentQRCodeLevel.value!!.toInt(),
                        restaurantName = currentQRCodeRestaurantName.value!!
//                        levelIcon = currentLevelImage.value!!
                    )
                    currentUserDocRef.collection(REWARD_CARD)
                        .add(newRewardCard)
                        .addOnSuccessListener {
                            i(TAG, "Add new reward card, document UID = ${it.id}")
                        }
//                    Toast.makeText(Owaste.instance.applicationContext, "卡片及點數新增成功！經驗值 + ${currentQRCodeLevel.value!!.toLong() * 10} exp！", Toast.LENGTH_SHORT).show()
                } else {

                    i(TAG, "get QuerySnapshot = ${querySnapshot.toObjects(RewardCard::class.java)}")

                    currentUserDocRef.collection(REWARD_CARD)
                        .whereEqualTo(CARD_ID, currentQRCodeCardId.value?.toLong())
                        .get().addOnSuccessListener {
                            val getCardIdDocRef = it.documents[0].reference
                            getCardIdDocRef.get().addOnSuccessListener { documentSnapshot ->

                                val currentPoints = documentSnapshot.get("points") as Long

                                getCardIdDocRef
                                    .update(mapOf("points" to currentPoints.plus(1)))
                                    .addOnSuccessListener {
                                        i(TAG, "get QuerySnapshot updated = ${querySnapshot.toObjects(RewardCard::class.java)}")
                                    }
                            }
                        }
                    Toast.makeText(Owaste.instance.applicationContext, "點數新增成功！經驗值 + ${currentQRCodeLevel.value!!.toLong() * 10} exp！", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun onQRCodeScannedUpdateExp() {

        currentUserDocRef.get()
            .addOnSuccessListener {
                val totalExp = it.get("exp") as Long
                i(TAG, "total Exp = $totalExp")
                currentUserDocRef.update(mapOf(
                        "exp" to totalExp.plus(currentQRCodeLevel.value!!.toLong() * 10)
                    )).addOnSuccessListener {

                i(TAG, "total Exp updated = $totalExp")
                }
            }
    }

    fun initUserLevelWhenBackToMap() {

        currentUserDocRef.get()
            .addOnSuccessListener {
                val totalExp = it.get("exp") as Long
                val userLevel: Int
//                i(TAG, "total Exp = $totalExp, current level = $currentLevel")
                userLevel = when {
                    totalExp < 99 -> 1
                    totalExp in 100..299 -> 2
                    totalExp in 300..599 -> 3
                    totalExp in 600..999 -> 4
                    totalExp in 1000..1499 -> 5
                    totalExp in 1500..2099 -> 6
                    totalExp in 2100..2799 -> 7
                    totalExp in 2800..3599 -> 8
                    totalExp in 3600..4499 -> 9
                    else -> 10
                }
                currentUserDocRef.update(mapOf(
                    "level" to userLevel
                )).addOnSuccessListener {
                    i(TAG, "current level = $userLevel")
                }
            }
    }

    fun getAllRewardCardsFromFirestore (listener: OnSuccessListener<QuerySnapshot>) {

        currentUserDocRef.collection(REWARD_CARD)
            .get().addOnSuccessListener(listener)
//            .addOnSuccessListener {
//                if (!it.isEmpty) {
//
//                    val allRewardCardsResult = it.toObjects(RewardCard::class.java)
//                    _allRewardCards.value = allRewardCardsResult
//                    i(TAG, "rewardCards.value = ${allRewardCards.value}")
//                } else {
//                    i(TAG, "該名使用者還沒有集點卡喔")
//                }
//            }
    }

    fun getCurrentUserExpToUpdateProgressBar (listener: OnSuccessListener<DocumentSnapshot>) {

        currentUserDocRef.get().addOnSuccessListener(listener)
    }
}