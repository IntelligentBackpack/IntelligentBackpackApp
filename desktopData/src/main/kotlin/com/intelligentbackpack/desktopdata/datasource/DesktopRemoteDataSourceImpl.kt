package com.intelligentbackpack.desktopdata.datasource

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.intelligentbackpack.accessdomain.entities.Email
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

class DesktopRemoteDataSourceImpl(
    baseUrl: String,
    backpackUrl: String,
    realtimeUrl: String,
) : DesktopRemoteDataSource {

    private val desktopApi = RetrofitHelper.getInstance(baseUrl).create(DesktopApi::class.java)
    private val backpackApi = RetrofitHelper.getInstance(backpackUrl).create(BackpackApi::class.java)
    private val database = Firebase.database(realtimeUrl)
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
            val backpacks = database.reference
                .child(user.email)
                .get().result
            val hasBackpack = backpacks.key != null
            val supplyInBackpack = backpacks.children
                .asSequence()
                .mapNotNull { it.getValue(String::class.java) }
                .map { it.uppercase() }
                .filter { RFIDPolicy.isValid(it) }
                .toSet()
            return Desktop.create(
                schoolSupplies = copies,
                schoolSuppliesInBackpack = copies.filter { it.rfidCode in supplyInBackpack }.toSet(),
                backpackAssociated = hasBackpack,
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
    override fun subscribeToBackpackChanges(email: Email, backpack: String) =
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
                                    .toSet()
                            )
                        )
                    }
                }
            }
            database.reference
                .child(email)
                .child(backpack)
                .addValueEventListener(postListener)

            awaitClose {
                database.reference
                    .child(email)
                    .child(backpack)
                    .removeEventListener(postListener)
            }
        }

    override fun associateBackpack(user: User, hash: String) {
        val response = backpackApi.associateBackpack(user.email, hash).execute()
        if (!response.isSuccessful) {
            throw DownloadException(getError(response))
        }
    }
}
