package com.cs407.secondserve.service

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.functions.ktx.functions

class LocationService {
    companion object {
        fun validateAddres(address: String): Boolean {
            return true;
        }

        fun distanceBetween(p1: GeoPoint, p2: GeoPoint): Double {
            return 0.0;
        }
    }
}