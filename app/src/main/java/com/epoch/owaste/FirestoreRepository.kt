package com.epoch.owaste

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FirestoreRepository {

    val TAG = "FIRESTORE_REPOSITORY"
    var firestoreDb = FirebaseFirestore.getInstance()
    var uid = UUID.randomUUID().toString()
    val restaurant = "Restaurant"

    //save restaurant to Firestore
    fun saveRestaurantItem (restaurantsItem: Restaurants): Task<Void> {

        var documentReference =
            firestoreDb.collection(restaurant)
                       .document(uid)
        return documentReference.set(restaurantsItem)
    }

    //get saved restaurant from Firestore
    fun getSavedRestaurant(): CollectionReference {

        return firestoreDb.collection(restaurant)
    }
}