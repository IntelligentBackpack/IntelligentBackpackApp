package com.intelligentbackpack.networkutility

import org.json.JSONObject
object ErrorHandler {
    fun getError(response: retrofit2.Response<*>, messageField: String = "message"): String {
        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
        return jsonObj.getString(messageField)
    }
}
