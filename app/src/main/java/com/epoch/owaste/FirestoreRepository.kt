package com.epoch.owaste

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    val TAG = "FIRESTORE_REPOSITORY"
    var firestoreDb = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    //save restaurants to firebase
    fun saveRestaurantItem (restaurants: Restaurants): Task<Void> {

        var documentReference =
            firestoreDb.collection("Restaurant")
                       .document()

        return documentReference.set(restaurants)
    }

}