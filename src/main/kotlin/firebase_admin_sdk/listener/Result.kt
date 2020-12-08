package firebase_admin_sdk.listener

sealed class Result<out R>
data class Success<out T>(val msg: String, val data: T) : Result<T>()
data class Failure(val msg: String) : Result<Nothing>()