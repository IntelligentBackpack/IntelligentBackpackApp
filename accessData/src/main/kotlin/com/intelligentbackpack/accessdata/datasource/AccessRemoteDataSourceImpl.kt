package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdata.adapter.Adapter.fromDomainToRemote
import com.intelligentbackpack.accessdata.adapter.Adapter.fromRemoteToDomain
import com.intelligentbackpack.accessdata.api.AccessApi
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.networkutility.DownloadException
import com.intelligentbackpack.networkutility.ErrorHandler.getError
import com.intelligentbackpack.networkutility.RetrofitHelper

/**
 * AccessRemoteDataStorageImpl is the implementation of AccessRemoteDataStorage.
 * @param baseUrl is the base url of the remote data storage.
 */
class AccessRemoteDataSourceImpl(baseUrl: String) : AccessRemoteDataSource {

    private val accessApi = RetrofitHelper.getInstance(baseUrl).create(AccessApi::class.java)

    /**
     * Creates a user.
     * @param user is the user to create.
     * @return the created user.
     * @throws DownloadException if the user cannot be created.
     */
    @Throws(DownloadException::class)
    override fun createUser(user: User): User {
        val response = accessApi.createNewUser(user.fromDomainToRemote()).execute()
        return if (response.isSuccessful) {
            response.body()!!.fromRemoteToDomain()
        } else {
            val error = getError(response)
            throw DownloadException(error)
        }
    }

    /**
     * Logs a user using email and password.
     * @param email is the user email.
     * @param password is the user password.
     * @return the logged user.
     * @throws DownloadException if the user cannot be logged.
     */
    @Throws(DownloadException::class)
    override fun accessWithData(email: String, password: String): User {
        val response = accessApi.getUser(
            access.communication.User.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build()
        ).execute()
        return if (response.isSuccessful) {
            response.body()!!.user.fromRemoteToDomain()
        } else {
            val error = getError(response)
            throw DownloadException(error)
        }
    }

    /**
     * Delete the user.
     * @throws DownloadException if the user cannot be deleted.
     */
    @Throws(DownloadException::class)
    override fun deleteUser(user: User) {
        val response = accessApi.deleteUser(user.fromDomainToRemote()).execute()
        if (!response.isSuccessful) {
            val error = getError(response)
            throw DownloadException(error)
        }
    }
}
