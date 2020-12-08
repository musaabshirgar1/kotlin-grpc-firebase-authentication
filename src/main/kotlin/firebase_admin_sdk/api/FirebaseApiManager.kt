package firebase_admin_sdk.api

import com.google.cloud.firestore.CollectionReference
import firebase_admin_sdk.FirebaseAdminSdk
import firebase_admin_sdk.listener.Failure
import firebase_admin_sdk.listener.Result
import firebase_admin_sdk.listener.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import service_utils.Utils.getFromDocumentSnapshot
import service_utils.Utils.getHashMap
import user_registration.User

object FirebaseApiManager : FirebaseAdminSdk() {

    private val userCollection: CollectionReference? =
        db?.collection(BaseUrl.USER)


    suspend fun storeUserData(
        user: User
    ): Result<String?> {
        return withContext(Dispatchers.IO) {
            try {
                val dr = userCollection?.document(user.id)

                val userMap = getHashMap(user)

                dr?.let { writeToFireStore(it, userMap) }

                return@withContext Success(msg = "Successful", data = user.id)
            } catch (e: Exception) {
                println(e.localizedMessage)
                Failure(msg = "Error occurred")
            }
        }
    }

    suspend fun getUserFromId(
        userId: String
    ): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                val documentSnapshot =
                    withContext(Dispatchers.IO) {
                        userCollection?.document(userId)?.get()?.get()
                    }

                val user = getFromDocumentSnapshot(data = documentSnapshot?.data)

                return@withContext Success(msg = "Successful", data = user)
            } catch (e: Exception) {
                println(e.localizedMessage)
                Failure(msg = "Error occurred")
            }
        }
    }

    object BaseUrl {
        const val USER = "users"
    }

}