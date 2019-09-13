package com.epoch.owaste.data

import android.util.Log.i
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.logging.Logger

object OwasteRepository {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("User/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    val _currentQRCodeLevel = MutableLiveData<String>()
    val currentQRCodeLevel: LiveData<String>
        get() = _currentQRCodeLevel

    val _currentQRCodeCardId = MutableLiveData<String>()
    val currentQRCodeCardId: LiveData<String>
        get() = _currentQRCodeCardId

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(
                    totalPoints = 0,
                    uid = FirebaseAuth.getInstance().currentUser!!.uid
                )
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                    i("Eltin_OwasteR","currentUserDocRef = $currentUserDocRef")
                }
            }
            else
                onComplete()
        }
    }
    fun onQRCodeScannedUpdateCard() {

    }
}