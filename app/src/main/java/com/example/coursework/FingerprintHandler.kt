package com.example.coursework

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi


@Suppress("DEPRECATION")
class FingerprintHandler(private val context: Context, private val listener: AuthResultListener, private val action: Action) :  FingerprintManager.AuthenticationCallback() {

    private lateinit var cancellationSignal: CancellationSignal

    fun startAuth(fingerprintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errorCode: Int,
                                       errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        Toast.makeText(context,
            "Authentication error: $errString", Toast.LENGTH_SHORT)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        listener.onAuthenticationSuccess(action)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context, "Authentication failed!",
            Toast.LENGTH_SHORT)
            .show()
    }
}