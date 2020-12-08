package user_registration

import com.google.protobuf.StringValue
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import user_registration.UserRegistrationGrpcKt.UserRegistrationCoroutineStub
import java.io.Closeable
import java.util.concurrent.TimeUnit

class UserRegistrationClient(private val channel: ManagedChannel) : Closeable {
    private val userStub: UserRegistrationCoroutineStub by lazy { UserRegistrationCoroutineStub(channel) }

    suspend fun registerUser(user: User): Boolean {
        userStub.registerUser(user).let {
            return if (it.value) {
                println("${user.firstName} is registered.")
                true
            } else {
                println("Failed to register the user.")
                false
            }
        }
    }

    suspend fun getUserInfo(userId: StringValue): User {
        userStub.getUserInfo(userId).let {
            return if (it.isInitialized) {
                println("Id: ${it.id}")
                println("First Name: ${it.firstName}")
                println("Last Name: ${it.lastName}")
                println("Mobile Number: ${it.mobileNumber}")
                println("Email: ${it.email}")
                println("Address: ${it.address}")
                println("Preferences: ${it.preferences}")
                println("Latitude: ${it.location.latitude}")
                println("Latitude: ${it.location.longitude}")
                it
            } else {
                println("No user found")
                User.getDefaultInstance()
            }
        }
    }

    suspend fun setUserCustomClaims(userId: StringValue): Boolean {
        userStub.setUserCustomClaim(userId).let {
            return if (it.value) {
                println("User with id: $userId has been authorized as an admin.")
                true
            } else {
                println("Error occurred")
                false
            }
        }
    }

    suspend fun isUserAdmin(userId: StringValue): Boolean {
        userStub.isUserAdmin(userId).let {
            return if (it.value) {
                println("User is Admin")
                true
            } else {
                println("User is not an Admin")
                false
            }
        }
    }

    suspend fun createCustomToken(userId: StringValue): String {
        userStub.createCustomToken(userId).let {
            return if (it.value != "Error occurred") {
                println("Custom Token for User with uId $userId: ${it.value}")
                it.value
            } else {
                println("Error occurred")
                it.value
            }
        }
    }

    suspend fun verifyIdToken(customToken: StringValue): String {
        userStub.verifyIdToken(customToken).let {
            return if (it.value != "Error occurred") {
                println("Id Token Decoded for User with uId: ${it.value}")
                it.value
            } else {
                println("Error occurred")
                it.value
            }
        }
    }


    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

}

suspend fun main() {
    val port = 50052
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val client = UserRegistrationClient(channel)
//    val location = Location.newBuilder()
//        .setLatitude(71121212)
//        .setLongitude(91122121)
//        .build()
//    val user = User.newBuilder()
//        .setId("dummyId")
//        .setFirstName("Musaab")
//        .setLastName("Shirgar")
//        .setPassword("123231241441")
//        .setEmail("musaab.shirgar45@gmail.com")
//        .setAddress("Address")
//        .setMobileNumber(123456789)
//        .setPreferences("Preferences")
//        .setLocation(location)
//        .build()
////    val userId = StringValue.newBuilder()
////        .setValue("oYX2ZuRYVHWklh3s64paRessHE02")
////        .build()
//    client.registerUser(user)
}
