package user_registration

import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import firebase_admin_sdk.api.FirebaseApiManager
import firebase_admin_sdk.listener.Failure
import firebase_admin_sdk.listener.Success
import io.grpc.Server
import io.grpc.ServerBuilder
import service_utils.Utils.buildBoolVal
import service_utils.Utils.buildStringVal
import service_utils.Utils.updateUserWithGeneratedId

class UserRegistrationServer(private val port: Int) {
    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(UserRegistrationService())
        .build()


    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@UserRegistrationServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class UserRegistrationService : UserRegistrationGrpcKt.UserRegistrationCoroutineImplBase() {

        override suspend fun registerUser(request: User): BoolValue {
            when (val result = FirebaseApiManager.registerNewUserWithEmailPassword(
                request.email,
                request.password
            )) {
                is Success -> {
                    val user = result.data?.let { request.updateUserWithGeneratedId(it) }
                    return when (user?.let { FirebaseApiManager.storeUserData(it) }) {
                        is Success -> {
                            buildBoolVal(true)
                        }
                        else -> {
                            buildBoolVal(false)
                        }
                    }
                }
                else -> {
                    return buildBoolVal(false)
                }
            }
        }

        override suspend fun getUserInfo(request: StringValue): User {
            var user: User? = null
            when (val result = FirebaseApiManager.getUserFromId(request.value)) {
                is Success -> {
                    println("Get User Info: ${result.msg}")
                    user = result.data
                }
                is Failure -> {
                    println("Get User Info: ${result.msg}")
                }
            }
            return user as User
        }

        override suspend fun setUserCustomClaim(request: StringValue): BoolValue {
            return when (val result = FirebaseApiManager.setUserCustomClaims(request.value)) {
                is Success -> {
                    println("Set User Custom Claims: ${result.msg}")
                    buildBoolVal(true)
                }
                is Failure -> {
                    println("Set User Custom Claims: ${result.msg}")
                    buildBoolVal(false)
                }
            }
        }

        override suspend fun isUserAdmin(request: StringValue): BoolValue {
            return when (val result = FirebaseApiManager.isUserAdmin(request.value)) {
                is Success -> {
                    println("Is User Admin: ${result.msg}")
                    buildBoolVal(result.data)
                }
                is Failure -> {
                    println("Is User Admin: ${result.msg}")
                    buildBoolVal(false)
                }
            }
        }

        override suspend fun createCustomToken(request: StringValue): StringValue {
            return when (val result = FirebaseApiManager.createCustomToken(request.value)) {
                is Success -> {
                    println("Custom Token Creation: ${result.msg}")
                    buildStringVal(result.data ?: "")
                }
                is Failure -> {
                    println("Custom Token Creation: ${result.msg}")
                    buildStringVal(result.msg)    //TODO: Do something better than this?
                }
            }
        }

        override suspend fun verifyIdToken(request: StringValue): StringValue {
            return when (val result = FirebaseApiManager.verifyIdToken(request.value)) {
                is Success -> {
                    println("Verify Id Token: ${result.msg}")
                    buildStringVal(result.data ?: "")
                }
                is Failure -> {
                    println("Verify Id Token: ${result.msg}")
                    buildStringVal(result.msg)    //TODO: Do something better than this?
                }
            }
        }
    }
}

fun main() {
    val port = 50052
    val server = UserRegistrationServer(port)
    server.start()
    server.blockUntilShutdown()
}