package com.example.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MainActivity : AppCompatActivity(), TakiInterfejs {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var cipher: Cipher
    private lateinit var secretKey: SecretKey

    companion object {
        const val KEY_NAME = "test"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, FingerPrintAuthenticationCallback(this, this))

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        generateSecretKey(KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build())

        readButton.setOnClickListener {
            cipher = getCipher()
            secretKey = getSecretKey()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipher() =
        Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)


    override fun onDataEncrypted(encryptedData: ByteArray) {
        println("zahashowane $encryptedData")
    }
}
