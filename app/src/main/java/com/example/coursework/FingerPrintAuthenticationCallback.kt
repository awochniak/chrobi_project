package com.example.coursework

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import java.nio.charset.Charset
import java.util.*


class FingerPrintAuthenticationCallback(private val context: Context, private val interfejs: TakiInterfejs) :  BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int,
                                       errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        Toast.makeText(context,
            "Błąd autentykacji: $errString", Toast.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal(
            "test".toByteArray(Charset.defaultCharset())
        )

        val oriString = "bezkoder tutorial"
        val encodedString =
            Base64.getEncoder().withoutPadding().encodeToString(oriString.toByteArray())

        println("enco1: $encodedString")

        val decodedBytes = Base64.getDecoder().decode(encodedString)
        val decodedString = String(decodedBytes)

        println("denco1: $decodedString")


//        val decryptedArray = result.cryptoObject?.cipher?.doFinal(decryptedInfo)
//        println("decodedData ${String(decryptedArray!!, Charset.defaultCharset())}")

        interfejs.onDataEncrypted(encryptedInfo!!)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context, "Autentykacja nieudana!",
            Toast.LENGTH_SHORT)
            .show()
    }
}