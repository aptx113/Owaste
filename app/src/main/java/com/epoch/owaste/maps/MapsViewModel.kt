package com.epoch.owaste.maps

import android.util.Log.*
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epoch.owaste.BuildConfig
import com.epoch.owaste.OwasteApi
import com.epoch.owaste.R
import com.epoch.owaste.data.PlaceDetails
import com.epoch.owaste.data.Restaurant
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList

class MapsViewModel: ViewModel() {

    private val TAG = "MAPS_VIEW_MODEL"
    private val RESTAURANT = "restaurants"
    var firestoreDb = FirebaseFirestore.getInstance()

    // filter the markers according to checkbox status
    val filterResultList : MutableList<Restaurant> = mutableListOf()

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants

    private val _dataList = MutableLiveData<List<Restaurant>>()
    val dataList: LiveData<List<Restaurant>>
        get() = _dataList

    private val _placeDetails = MutableLiveData<PlaceDetails>()
    val placeDetails: LiveData<PlaceDetails>
        get() = _placeDetails

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
//        getRestaurantsFromFirestore()
        i(TAG, "LiveData<List<Restaurant>> = ${restaurants.value}")
        getPlaceDetails()
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
    private fun getSavedRestaurantsRef(): CollectionReference {

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
            _dataList.value = savedRestaurantList
            _restaurants.value = savedRestaurantList
            i(TAG, "savedRestaurantsList = ${_restaurants.value}")
        })

        return _restaurants
    }




//    val onCheckedChangeListener = 1
    fun onCheckedChangeListener() = CompoundButton.OnCheckedChangeListener { checkBox, isChecked ->

            val level1 = dataList.value?.filter { it.level == 1 } as ArrayList<Restaurant>
            val level2 = dataList.value?.filter { it.level == 2 } as ArrayList<Restaurant>
            val level3 = dataList.value?.filter { it.level == 3 } as ArrayList<Restaurant>
            val level4 = dataList.value?.filter { it.level == 4 } as ArrayList<Restaurant>
            val level5 = dataList.value?.filter { it.level == 5 } as ArrayList<Restaurant>

            i(TAG, "lv1 = $level1")
            i(TAG, "lv2 = $level2")
            i(TAG, "lv3 = $level3")
            i(TAG, "lv4 = $level4")
            i(TAG, "lv5 = $level5")

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
            i(TAG, "filterResultList = $filterResultList")
        }

    private fun getPlaceDetails() {

        coroutineScope.launch {

            val getResultDeferred =
                OwasteApi.retrofitService.getPlaceDetailsAsync(
                    placeId = "ChIJcQtDtb6rQjQRT-zRc6G1-D4",
                    fields = "formatted_address,formatted_phone_number,name,place_id,rating,reviews",
                    key = BuildConfig.API_KEY,
                    language = "zh-TW"
                )

            try {
                val placeDetailsResult = getResultDeferred.await()
                _placeDetails.value = placeDetailsResult.result
            } catch (e: Exception) {
                i(TAG, "exception = ${e.message}")
            }
        }
    }
}