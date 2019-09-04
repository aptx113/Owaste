package com.epoch.owaste

import android.util.Log.e
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FirestoreViewModel: ViewModel() {

    val TAG = "FIRESTORE_VIEW_MODEL"
    var firestoreRepository = FirestoreRepository()
    var savedRestaurants: MutableLiveData<List<Restaurants>> = MutableLiveData()

    // save restaurants to firestore
    fun saveRestaurantToFirestore(restaurants: Restaurants) {
        firestoreRepository.saveRestaurantItem(restaurants).addOnFailureListener {
            e(TAG, "Failed to save restaurant !")
        }
    }
}