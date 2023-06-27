package com.intelligentbackpack.networkutility

import org.json.JSONObject

/**
 * ErrorHandler is the object that handles the errors for json responses in retrofit.
 */
object ErrorHandler {
    /**
     * Gets the error message from the response.
     *
     * @param response is the response to get the error message from.
     * @param messageField is the field of the json object that contains the error message.
     * @return the error message.
     */
    fun getError(response: retrofit2.Response<*>, messageField: String = "message"): String {
        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
        return jsonObj.getString(messageField)
    }
}
