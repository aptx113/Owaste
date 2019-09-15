package com.epoch.owaste.Maps

import android.util.Log.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.data.Restaurant
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MapsViewModel: ViewModel() {

    val TAG = "MAPS_VIEW_MODEL"
    val RESTAURANT = "restaurants"
    var firestoreDb = FirebaseFirestore.getInstance()

    val _restaurants = MutableLiveData<List<Restaurant>>()

    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants

    init {
        getRestaurantsFromFirestore()
        i(TAG, "LiveData<List<Restaurant>> = ${restaurants.value}")
    }

    //add restaurants to Firestore
    fun addRestaurant (restaurant: Restaurant) {

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

    //get saved restaurants from Firestore
    fun getSavedRestaurantsRef(): CollectionReference {

        return firestoreDb.collection(RESTAURANT)
    }

    // save restaurants to firestore
//    fun saveRestaurantToFirestore(restaurants: Restaurant) {
//        addRestaurant(restaurants)
//    }

    fun getRestaurantsFromFirestore(): LiveData<List<Restaurant>> {

        getSavedRestaurantsRef().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->

            if (e != null) {
                w(TAG, "Listen failed", e)
                _restaurants.value = null
                return@EventListener
            }

            val savedRestaurantList: MutableList<Restaurant> = mutableListOf()
            for (doc in value!!) {
                val restaurant = doc.toObject(Restaurant::class.java)
                savedRestaurantList.add(restaurant)
            }
            _restaurants.value = savedRestaurantList
            i(TAG, "savedRestaurantsList = ${_restaurants.value}")
        })

        return _restaurants
    }
}