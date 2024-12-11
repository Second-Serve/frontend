package com.cs407.secondserve.service

import android.util.Log
import com.cs407.secondserve.model.AccountType
import com.cs407.secondserve.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountService {
    companion object {
        lateinit var auth: FirebaseAuth

        var currentUser: User? = null

        fun register(
            email: String,
            password: String,
            accountType: AccountType,
            firstName: String? = null,
            lastName: String? = null,
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
                    )

                    // If the account we're registering is a customer, it should have a name
                    if (accountType == AccountType.CUSTOMER) {
                        userInfo["first_name"] = firstName!!
                        userInfo["last_name"] = lastName!!
                    }

                    // Try to add additional user info to the database
                    val uid = task.user!!.uid
                    Firebase.firestore.collection("users")
                        .document(uid)
                        .set(userInfo)
                        .addOnSuccessListener {
                            val user = User(
                                id = uid,
                                accountType,
                                email
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
                                        currentUser = user
                                        onSuccess?.invoke(user)
                                    },
                                    onFailure = { exception ->
                                        onFailure?.invoke(exception)
                                    }
                                )
                            } else {
                                currentUser = user
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
            onSuccess: ((FirebaseUser, User) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    signIn(result.user!!, onSuccess, onFailure)
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }

        fun signIn(
            authUser: FirebaseUser,
            onSuccess: ((FirebaseUser, User) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            getUserById(
                authUser.uid,
                onSuccess = { user ->
                    currentUser = user
                    onSuccess?.invoke(authUser, user)
                },
                onFailure = { exception ->
                    onFailure?.invoke(exception)
                }
            )
        }

        private fun getUserById(
            userId: String,
            onSuccess: ((User) -> Unit)? = null,
            onFailure: ((Exception) -> Unit)? = null
        ) {
            val db = Firebase.firestore
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDocument ->
                    Log.d("AccountService", "User data: $userDocument")

                    val accountType = AccountType.fromString(userDocument.data!!["account_type"] as String)

                    var user = User(
                        userDocument.id,
                        accountType,
                        "todo@email.address", // TODO: Get email from database
                    )

                    if (accountType == AccountType.BUSINESS) {
                        RestaurantService.fetchByUserId(
                            userId,
                            onSuccess = { restaurant ->
                                user.restaurant = restaurant
                                onSuccess?.invoke(user)
                            },
                            onFailure = { exception ->
                                onFailure?.invoke(exception)
                            }
                        )
                    } else {
                        user.firstName = userDocument.data!!["first_name"] as String?
                        user.lastName = userDocument.data!!["last_name"] as String?
                        onSuccess?.invoke(user)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }
    }
}