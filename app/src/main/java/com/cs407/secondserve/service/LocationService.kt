package com.cs407.secondserve.service

import android.util.Log
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class LocationService {
    companion object {
        public const val TAG = "LocationService"

        suspend fun validateAddress(address: String): Boolean {
            if (address.isBlank()) {
                Log.d(TAG, "Address is empty or null")
                return false
            }

            try {
                val data = hashMapOf("address" to address)
                val result = Firebase.functions
                    .getHttpsCallable("isAddressValid")
                    .call(data)
                    .await()
                val resultMap = result.getData() as? Map<*, *>
                val isValid = resultMap?.get("isValid") as? Boolean ?: false

                Log.d(TAG, "Address validation result for '$address': $isValid")
                return isValid
            } catch (e: Exception) {
                Log.e(TAG, "Error validating address: ${e.message}", e)
                return false
            }
        }
    }
}


//
//            fun distanceBetween(p1: GeoPoint, p2: GeoPoint): Double {
//                return 0.0;
//            }
//