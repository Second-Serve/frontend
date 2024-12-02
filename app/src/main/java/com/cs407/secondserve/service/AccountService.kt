package com.cs407.secondserve.service

import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountService {
    companion object {
        lateinit var auth: FirebaseAuth

        fun register(
            email: String,
            password: String,
            firstName: String,
            lastName: String,
            accountType: AccountType,
            restaurantName: String? = null,
            address: String? = null,
            pickupStartTime: String? = null,
            pickupEndTime: String? = null,
            onSuccess: ((User) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { task ->
                    val userInfo = hashMapOf(
                        "account_type" to accountType.toString(),
                        "first_name" to firstName,
                        "last_name" to lastName
                    )

                    // Try to add additional user info to the database
                    val uid = task.user!!.uid
                    Firebase.firestore.collection("users")
                        .document(uid)
                        .set(userInfo)
                        .addOnSuccessListener {
                            val user = User(
                                id = uid,
                                accountType,
                                email,
                                firstName,
                                lastName
                            )

                            // Have to create a restaurant if we're a business account
                            if (accountType == AccountType.BUSINESS) {
                                RestaurantService.create(
                                    user,
                                    restaurantName!!,
                                    address!!,
                                    pickupStartTime!!,
                                    pickupEndTime!!,
                                    onSuccess = {
                                        onSuccess?.invoke(user)
                                    },
                                    onFailure = { exception ->
                                        onFailure?.invoke(exception)
                                    }
                                )
                            } else {
                                onSuccess?.invoke(user)
                            }
                        }
                        .addOnFailureListener { exception ->
                            /* What? We were successful in registering a new user, but we failed to
                             * add additional info to the database, leaving us in purgatory. Nothing
                             * we can do about that.
                             */
                            onFailure?.invoke(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }

        fun signIn(
            email: String,
            password: String,
            onSuccess: ((AuthResult) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { task ->
                    onSuccess?.invoke(task)
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }
    }
}