package com.epoch.owaste.Maps

import android.util.Log.*
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.R
import com.epoch.owaste.data.Restaurant
import com.epoch.owaste.data.restaurantsList
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

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




//    val onCheckedChangeListener = 1
    fun onCheckedChangeListener() = CompoundButton.OnCheckedChangeListener { checkBox, isChecked ->

            // filter the markers according to checkbox status
            val filterResultList = ArrayList<Restaurant>()
            val level1 = restaurants.value!!.filter { it.level == 1 } as ArrayList<Restaurant>
            val level2 = restaurants.value!!.filter { it.level == 2 } as ArrayList<Restaurant>
            val level3 = restaurants.value!!.filter { it.level == 3 } as ArrayList<Restaurant>
            val level4 = restaurants.value!!.filter { it.level == 4 } as ArrayList<Restaurant>
            val level5 = restaurants.value!!.filter { it.level == 5 } as ArrayList<Restaurant>

            when (checkBox?.id) {
                R.id.cb_lv1 -> if (isChecked) {
                    filterResultList.addAll(level1)
                    i(TAG, "Add Lv1 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level1)
                    i(TAG, "Remove Lv1 from filterResultList = $filterResultList !")
                }
                R.id.cb_lv2 -> if (isChecked) {
                    filterResultList.addAll(level2)
                    i(TAG, "Add Lv2 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level2)
                    i(TAG, "Remove Lv2 from filterResultList = $filterResultList !")
                }
                R.id.cb_lv3 -> if (isChecked) {
                    filterResultList.addAll(level3)
                    i(TAG, "Add Lv3 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level3)
                    i(TAG, "Remove Lv3 from filterResultList = $filterResultList !")
                }
                R.id.cb_lv4 -> if (isChecked) {
                    filterResultList.addAll(level4)
                    i(TAG, "Add Lv4 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level4)
                    i(TAG, "Remove Lv4 from filterResultList = $filterResultList !")
                }
                R.id.cb_lv5 -> if (isChecked) {
                    filterResultList.addAll(level5)
                    i(TAG, "Add Lv5 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level5)
                    i(TAG, "Remove Lv5 from filterResultList = $filterResultList !")
                }
            }
            _restaurants.value = filterResultList
        }
}