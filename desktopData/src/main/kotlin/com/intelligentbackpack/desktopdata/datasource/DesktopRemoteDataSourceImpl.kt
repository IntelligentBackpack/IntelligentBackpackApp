package com.intelligentbackpack.desktopdata.datasource

import book.communication.BasicMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.desktopdata.adapter.BookAdapter.fromRemoteToDomain
import com.intelligentbackpack.desktopdata.adapter.SchoolSupplyAdapter.fromDomainToRemote
import com.intelligentbackpack.desktopdata.adapter.SchoolSupplyAdapter.fromRemoteToDomain
import com.intelligentbackpack.desktopdata.api.BackpackApi
import com.intelligentbackpack.desktopdata.api.DesktopApi
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.policies.RFIDPolicy
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler.getError
import com.intelligentbackpack.networkutility.RetrofitHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.RequestBody
import org.json.JSONObject

class DesktopRemoteDataSourceImpl(
    baseUrl: String,
    backpackUrl: String,
    realtimeUrl: String,
) : DesktopRemoteDataSource {

    private val desktopApi = RetrofitHelper.getInstance(baseUrl).create(DesktopApi::class.java)
    private val backpackApi = RetrofitHelper.getInstance(backpackUrl).create(BackpackApi::class.java)
    private val database = Firebase.database(realtimeUrl)

    private fun getUserBackpackFirebaseReference(user: User) =
        database.reference
            .child(user.email.replace(".", "-"))

    override fun getBook(isbn: String): Book? {
        val response = desktopApi.getBook(isbn).execute()
        if (response.isSuccessful) {
            return response.body()?.fromRemoteToDomain()
        } else {
            throw DownloadException(getError(response))
        }
    }

    override fun getDesktop(user: User): Desktop {
        val response = desktopApi.getLibrary(user.email).execute()
        if (response.isSuccessful) {
            val books = response.body()
                ?.copiesList
                ?.mapNotNull { getBook(it.isbn) }
                ?.toSet()
                ?: emptySet()
            val copies = response.body()
                ?.copiesList
                ?.map { it.fromRemoteToDomain(books) }
                ?.toSet()
                ?: emptySet()
            val firebaseRequest = getUserBackpackFirebaseReference(user).get()
            runBlocking {
                firebaseRequest.await()
            }
            val result = firebaseRequest.result
            val backpacks = result.children.asSequence().map { it.key }.filterNotNull().toSet()
            val supplyInBackpack = result.children
                .asSequence()
                .map { it.key to it.value }
                .filter { it.first != null }
                .filter { it.second is Map<*, *> }
                .map { it.first to it.second as Map<*, *> }
                .map { it.first to it.second.keys }
                .map {
                    it.first to it.second
                        .map { rfid -> rfid as String }
                        .map { rfid -> rfid.uppercase() }
                        .filter { rfid -> RFIDPolicy.isValid(rfid) }
                }
                .filter { it.first != null }
                .associate { it.first!! to it.second }
            return Desktop.create(
                schoolSupplies = copies,
                schoolSuppliesInBackpack = copies
                    .filter { it.rfidCode in supplyInBackpack.values.flatten() }
                    .toSet(),
                backpack = backpacks.firstOrNull(),
            )
        } else {
            throw DownloadException(getError(response))
        }
    }

    override fun addSchoolSupply(user: User, schoolSupply: SchoolSupply) {
        if (schoolSupply is BookCopy) {
            val copy = schoolSupply.fromDomainToRemote(user)
            val response = desktopApi.addBookCopy(copy).execute()
            if (!response.isSuccessful) {
                throw DownloadException(getError(response))
            }
        } else {
            throw DownloadException("School supply not supported")
        }
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToBackpackChanges(user: User, backpack: String) =
        callbackFlow<Result<Set<String>>> {
            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    runBlocking {
                        this@callbackFlow.send(Result.failure(error.toException()))
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val items = dataSnapshot.children.map { it.key }
                    runBlocking {
                        this@callbackFlow.send(
                            Result.success(
                                items
                                    .filterNotNull()
                                    .map { it.uppercase() }
                                    .filter { RFIDPolicy.isValid(it) }
                                    .toSet(),
                            ),
                        )
                    }
                }
            }
            getUserBackpackFirebaseReference(user)
                .child(backpack)
                .addValueEventListener(postListener)

            awaitClose {
                getUserBackpackFirebaseReference(user)
                    .child(backpack)
                    .removeEventListener(postListener)
            }
        }

    override fun associateBackpack(user: User, hash: String): String {
        val jsonParam = mapOf(Pair("email", user.email))
        val request = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            (JSONObject(jsonParam)).toString(),
        )
        val response = backpackApi.associateBackpack(request, hash).execute()
        if (response.isSuccessful) {
            return hash
        } else {
            throw DownloadException(response.errorBody()?.string() ?: "")
        }
    }

    override fun disassociateBackpack(user: User, hash: String) {
        val jsonParam = mapOf(Pair("email", user.email))
        val request = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            (JSONObject(jsonParam)).toString(),
        )
        val response = backpackApi.disassociateBackpack(request, hash).execute()
        if (!response.isSuccessful) {
            throw DownloadException(response.errorBody()?.string() ?: "")
        }
    }

    override fun deleteDesktop(user: User) {
        val message = BasicMessage.newBuilder().setMessage(user.email).build()
        val response = desktopApi.deleteAllCopies(message).execute()
        if (response.isSuccessful) {
            val responseLibrary = desktopApi.deleteLibrary(message).execute()
            if (!responseLibrary.isSuccessful) {
                throw DownloadException(getError(response))
            }
        } else {
            throw DownloadException(getError(response))
        }
    }
}
