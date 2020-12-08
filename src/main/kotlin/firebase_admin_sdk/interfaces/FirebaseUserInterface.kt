package firebase_admin_sdk.interfaces

import com.google.firebase.auth.UserRecord
import firebase_admin_sdk.listener.Result

interface FirebaseUserInterface {
    suspend fun registerNewUserWithEmailPassword(
        userEmail: String,
        password: String

    ): Result<String?>

    suspend fun getUserById(uId: String): Result<UserRecord?>

    suspend fun isUserAdmin(uId: String): Result<Boolean>

    suspend fun setUserCustomClaims(uId: String): Result<String?>

    suspend fun getUserByEmail(userEmail: String): Result<UserRecord?>

    suspend fun deleteUserById(uId: String)

    suspend fun listAllUsers(): Result<List<UserRecord>>

    suspend fun createCustomToken(uId: String): Result<String?>

    suspend fun verifyIdToken(customToken: String): Result<String?>
}