package com.epoch.owaste.maps

import android.content.SharedPreferences
import android.util.Log.*
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epoch.owaste.OwasteApi
import com.epoch.owaste.R
import com.epoch.owaste.data.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.exp

class MapsViewModel : ViewModel() {

    private val TAG = "Eltin_" + this.javaClass.simpleName
    private val RESTAURANT = "restaurants"
    private var firestoreDb = FirebaseFirestore.getInstance()

    val savedRestaurantList: MutableList<Restaurant> = mutableListOf()
    // filter the markers according to checkbox status
    private val filterResultList: MutableList<Restaurant> = mutableListOf()

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants

    private val _filterDataList = MutableLiveData<List<Restaurant>>()
    val filterDataList: LiveData<List<Restaurant>>
        get() = _filterDataList

    private val _placeDetails = MutableLiveData<PlaceDetails>()
    val placeDetails: LiveData<PlaceDetails>
        get() = _placeDetails

    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>>
        get() = _photos

    private val _reviews = MutableLiveData<List<PlaceReviews>>()
    val reviews: LiveData<List<PlaceReviews>>
        get() = _reviews

    init {
        i(TAG, "LiveData<List<Restaurant>> = ${restaurants.value}")
    }

    // to avoid showing data of last place when calling MapsFragment.onMarkerClick()
    fun resetRestaurantDetailsToNull() {
        _placeDetails.value = null
        _photos.value = null
    }

    // get saved restaurants reference from Firestore
    private fun getSavedRestaurantsRef(): CollectionReference {

        return firestoreDb.collection(RESTAURANT)
    }

    // get saved restaurants from Firestore
    fun getRestaurantsFromFirestore(): LiveData<List<Restaurant>> {

        getSavedRestaurantsRef().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->

            if (e != null) {

                w(TAG, "Listen failed", e)
                _restaurants.value = null
                return@EventListener
            }

            value?.let {

                for (doc in value) {
                    i(TAG, "doc = ${doc.data}")
                    val restaurant = doc.toObject(Restaurant::class.java)
                    savedRestaurantList.add(restaurant)
                }
                _filterDataList.value = savedRestaurantList
                _restaurants.value = savedRestaurantList
                i(TAG, "savedRestaurantsList = ${_restaurants.value}")
            }
        })

        return _restaurants
    }

    fun onCheckedChangeListener() = CompoundButton.OnCheckedChangeListener { checkBox, isChecked ->

        // move outside
        val level1 = filterDataList.value?.filter { it.level == 1 } as ArrayList<Restaurant>
        val level2 = filterDataList.value?.filter { it.level == 2 } as ArrayList<Restaurant>
        val level3 = filterDataList.value?.filter { it.level == 3 } as ArrayList<Restaurant>
        val level4 = filterDataList.value?.filter { it.level == 4 } as ArrayList<Restaurant>
        val level5 = filterDataList.value?.filter { it.level == 5 } as ArrayList<Restaurant>

        i(TAG, "lv1 = $level1")
        i(TAG, "lv2 = $level2")
        i(TAG, "lv3 = $level3")
        i(TAG, "lv4 = $level4")
        i(TAG, "lv5 = $level5")

        when (checkBox?.id) {
            R.id.cb_lv1 ->
                if (isChecked) {
                    filterResultList.addAll(level1)
                    i(TAG, "Add Lv1 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level1)
                    i(TAG, "Remove Lv1 from filterResultList = $filterResultList !")
                }
            R.id.cb_lv2 ->
                if (isChecked) {
                    filterResultList.addAll(level2)
                    i(TAG, "Add Lv2 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level2)
                    i(TAG, "Remove Lv2 from filterResultList = $filterResultList !")
                }
            R.id.cb_lv3 ->
                if (isChecked) {
                    filterResultList.addAll(level3)
                    i(TAG, "Add Lv3 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level3)
                    i(TAG, "Remove Lv3 from filterResultList = $filterResultList !")
                }
            R.id.cb_lv4 ->
                if (isChecked) {
                    filterResultList.addAll(level4)
                    i(TAG, "Add Lv4 to filterResultList = $filterResultList !")
                } else {
                    filterResultList.removeAll(level4)
                    i(TAG, "Remove Lv4 from filterResultList = $filterResultList !")
                }
            R.id.cb_lv5 ->
                if (isChecked) {
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

    fun getPlaceDetails(placeId: String) {

        viewModelScope.launch(Dispatchers.Main) {

            val getResultDeferred =
                OwasteApi.retrofitService.getPlaceDetailsAsync(placeId)

            try {
                val placeDetailsResult = getResultDeferred.await()
                _placeDetails.value = placeDetailsResult.result
                i(TAG, "placeDetails = ${placeDetails.value}")
                _photos.value = placeDetails.value?.photos
                i(TAG, "photos = ${photos.value}")
                _reviews.value = placeDetails.value?.reviews
                i(TAG, "reviews = ${reviews.value}")

            } catch (e: Exception) {
                i(TAG, "exception = ${e.message}")
            }
        }
    }

    fun getClickedRestaurantFromFirestoreByName(
        title: String,
        listener: OnSuccessListener<QuerySnapshot>
    ) {

        getSavedRestaurantsRef()
            .whereEqualTo("name", title)
            .get()
            .addOnSuccessListener(listener)

    }

    fun getCurrentUserExpToUpdateProgressBar(listener: OnSuccessListener<DocumentSnapshot>) {

        OwasteRepository.getCurrentUserExpToUpdateProgressBar(listener)
    }

    fun clearFilter() {

        _restaurants.value = savedRestaurantList
        i(TAG, "restaurants.value = ${restaurants.value}")
    }
}