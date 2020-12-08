package service_utils

import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import user_registration.Location
import user_registration.User

object Utils {
    fun User.updateUserWithGeneratedId(uId: String): User = User.newBuilder()
        .setId(uId)
        .setFirstName(this.firstName)
        .setLastName(this.lastName)
        .setMobileNumber(this.mobileNumber)
        .setLocation(this.location)
        .setEmail(this.email)
        .setPassword(this.password)
        .setPreferences(this.preferences)
        .setAddress(this.address)
        .build()

    fun buildBoolVal(data: Boolean): BoolValue =
        BoolValue.newBuilder()
            .setValue(data)
            .build()

    fun buildStringVal(data: String): StringValue =
        StringValue.newBuilder()
            .setValue(data)
            .build()

    fun getHashMap(user: User): MutableMap<String, Any?>? {
        return mutableMapOf(
            "id" to user.id,
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "address" to user.address,
            "preferences" to user.preferences,
            "mobileNumber" to user.mobileNumber,
            "latitude" to user.location.latitude,
            "longitude" to user.location.longitude
        )
    }

    fun getFromDocumentSnapshot(data: MutableMap<String, Any?>?): User {
        return User.newBuilder()
            .setId(data?.get("id") as String?)
            .setFirstName(data?.get("firstName") as String?)
            .setLastName(data?.get("lastName") as String?)
            .setEmail(data?.get("email") as String?)
            .setAddress(data?.get("address") as String?)
            .setPreferences(data?.get("preferences") as String?)
            .setLocation(
                Location.newBuilder()
                    .setLatitude((data?.get("latitude") as Number?)?.toInt() ?: 0)
                    .setLongitude((data?.get("longitude") as Number?)?.toInt() ?: 0)
                    .build()
            )
            .setMobileNumber((data?.get("mobileNumber") as Number?)?.toLong() ?: 0L)
            .build()
    }
}