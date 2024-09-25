package com.payu.payuppisdksample

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashGenerationUtils {
    fun generateHashFromSDK(
        hashData: String,
        salt: String?
    ): String? {

        return calculateHash("$hashData$salt")
    }

    /**
     * Function to calculate the SHA-512 hash
     * @param hashString hash string for hash calculation
     * @return Post Data containig the
     * */
    private fun calculateHash(hashString: String): String {
        val messageDigest =
            MessageDigest.getInstance("SHA-512")
        messageDigest.update(hashString.toByteArray())
        val mdbytes = messageDigest.digest()
        return getHexString(mdbytes)
    }

    private fun getHexString(data: ByteArray): String {
        // Create Hex String
        val hexString: StringBuilder = StringBuilder()
        for (aMessageDigest: Byte in data) {
            var h: String = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2)
                h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    }
}