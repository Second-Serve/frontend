package com.cs407.secondserve.model

class User(
    var id: String,
    var accountType: AccountType,
    var email: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var campusId: Int? = null,
    var restaurant: Restaurant? = null
)
