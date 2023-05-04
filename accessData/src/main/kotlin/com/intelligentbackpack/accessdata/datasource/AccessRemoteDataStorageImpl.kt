package com.intelligentbackpack.accessdata.datasource

import com.intelligentbackpack.accessdata.adapter.Adapter.fromDomainToRemote
import com.intelligentbackpack.accessdata.api.AccessApi
import com.intelligentbackpack.accessdata.api.RetrofitHelper
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.accessdata.adapter.Adapter.fromRemoteToDomain
import com.intelligentbackpack.accessdata.exception.DownloadException
import org.json.JSONObject

class AccessRemoteDataStorageImpl(baseUrl: String) : AccessRemoteDataStorage {

    private val accessApi = RetrofitHelper.getInstance(baseUrl).create(AccessApi::class.java)

    override fun createUser(user: User): User {
        val response = accessApi.createNewUser(user.fromDomainToRemote()).execute()
        return if (response.isSuccessful) {
            response.body()!!.fromRemoteToDomain()
        } else {
            val error = getError(response)
            throw DownloadException(error)
        }
    }

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

    override fun deleteUser(user: User) {
        val response = accessApi.deleteUser(user.fromDomainToRemote()).execute()
        if (!response.isSuccessful) {
            val error = getError(response)
            throw DownloadException(error)
        }
    }

    private fun getError(response: retrofit2.Response<*>): String {
        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
        return jsonObj.getString("message")
    }
}
