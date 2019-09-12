package com.epoch.owaste

import android.util.Log.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.data.Restaurants
import com.epoch.owaste.data.restaurantsList
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MapsViewModel: ViewModel() {

    val TAG = "MAPS_VIEW_MODEL"
    val RESTAURANT = "Restaurant"
    var firestoreDb = FirebaseFirestore.getInstance()
    val savedRestaurants = MutableLiveData<List<Restaurants>>()
//    var uid = UUID.randomUUID().toString()

    val _restaurants = MutableLiveData<List<Restaurants>>()

    val restaurants: LiveData<List<Restaurants>>
        get() = _restaurants

    init {
        _restaurants.value = restaurantsList
        i(TAG, "LiveData<List<Restaurants>> = ${restaurants.value}")
    }
    //add restaurant to Firestore
    fun addRestaurant (restaurant: Restaurants) {

        firestoreDb.collection(RESTAURANT)
            .add(restaurant)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    i(TAG, "added successfully\n" + it.result?.id)
                } else {
                    i(TAG, "fail to add \n" + it.exception.toString())
                }
            }
    }

    //get saved restaurant from Firestore
    fun getSavedRestaurant(): CollectionReference {

        return firestoreDb.collection(RESTAURANT)
    }

    // save restaurant to firestore
//    fun saveRestaurantToFirestore(restaurants: Restaurants) {
//        addRestaurant(restaurants)
//    }

    fun getSavedRestaurants(): LiveData<List<Restaurants>> {
        getSavedRestaurant().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->

            if (e != null) {
                w(TAG, "Listen failed", e)
                savedRestaurants.value = null
                return@EventListener
            }

            var savedRestaurantsList: MutableList<Restaurants> = mutableListOf()
            for (doc in value!!) {
                var restaurant = doc.toObject(Restaurants::class.java)
                savedRestaurantsList.add(restaurant)
            }
            savedRestaurants.value = savedRestaurantsList
        })

        return savedRestaurants
    }

    fun addUserToFirebase() {

    }
}