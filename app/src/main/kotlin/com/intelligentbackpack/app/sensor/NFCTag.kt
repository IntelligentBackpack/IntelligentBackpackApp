package com.intelligentbackpack.app.sensor

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.util.Log
import java.io.IOException

import java.nio.charset.Charset


data class NFCTag(val rfidId: String, val rfidData: String?) {

    companion object {

        private const val TAG = "NFCTag"

        fun detectTagData(tag: Tag): NFCTag {
            val sb = StringBuilder()
            val id = tag.id
            val hexId = toHex(id)
            var data: String? = null
            sb.append("ID (hex): ").append(hexId).append('\n')
            //sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
            //sb.append("ID (dec): ").append(toDec(id)).append('\n')
            //sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
            val prefix = "android.nfc.tech."
            sb.append("Technologies: ")
            for (tech in tag.techList) {
                sb.append(tech.substring(prefix.length))
                sb.append(", ")
            }
            sb.delete(sb.length - 2, sb.length)
            for (tech in tag.techList) {
                if (tech == MifareClassic::class.java.name) {
                    sb.append('\n')
                    var type = "Unknown"
                    try {
                        val mifareTag = MifareClassic.get(tag)
                        when (mifareTag.type) {
                            MifareClassic.TYPE_CLASSIC -> type = "Classic"
                            MifareClassic.TYPE_PLUS -> type = "Plus"
                            MifareClassic.TYPE_PRO -> type = "Pro"
                        }
                        sb.append("Mifare Classic type: ")
                        sb.append(type)
                        sb.append('\n')
                        sb.append("Mifare size: ")
                        sb.append(mifareTag.size.toString() + " bytes")
                        sb.append('\n')
                        sb.append("Mifare sectors: ")
                        sb.append(mifareTag.sectorCount)
                        sb.append('\n')
                        sb.append("Mifare blocks: ")
                        sb.append(mifareTag.blockCount)
                    } catch (e: Exception) {
                        sb.append("Mifare classic error: " + e.message)
                    }
                }
                if (tech == MifareUltralight::class.java.name) {
                    sb.append('\n')
                    val mifareUlTag = MifareUltralight.get(tag)
                    var type = "Unknown"
                    when (mifareUlTag.type) {
                        MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                        MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                    }
                    sb.append("Mifare Ultralight type: ")
                    sb.append(type)
                    data = readTag(mifareUlTag)
                }
            }
            Log.v(TAG, sb.toString())
            return NFCTag(hexId, data)
        }

        private fun readTag(mifareUlTag: MifareUltralight?): String? {
            try {
                mifareUlTag!!.connect()
                val payload = mifareUlTag.readPages(4)
                return String(payload, Charset.forName("US-ASCII"))
            } catch (e: IOException) {
                Log.e(TAG, "IOException while reading MifareUltralight message...", e)
            } finally {
                if (mifareUlTag != null) {
                    try {
                        mifareUlTag.close()
                    } catch (e: IOException) {
                        Log.e(TAG, "Error closing tag...", e)
                    }
                }
            }
            return null
        }

        private fun toHex(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (i in bytes.indices.reversed()) {
                val b = bytes[i].toInt() and 0xff
                if (b < 0x10) sb.append('0')
                sb.append(Integer.toHexString(b))
                if (i > 0) {
                    sb.append(":")
                }
            }
            return sb.toString().uppercase()
        }
    }
}
