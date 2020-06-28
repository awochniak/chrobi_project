package com.example.coursework

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), AuthResultListener {

    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var cryptoObjectEncrypt: FingerprintManager.CryptoObject
    private lateinit var cryptoObjectDecrypt: FingerprintManager.CryptoObject
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val separator = "-"
    private val encryptionObject = EncryptionObject.newInstance()

    private var encryptedMessage: String = ""

    companion object {
        private const val SECURE_KEY = "data.source.prefs.SECURE_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        pref = this.getSharedPreferences(
            "com.example.coursework.pref",
            Context.MODE_PRIVATE
        )

        editor = pref.edit()

        saveButton.setOnClickListener {
            authToEncrypt()
        }

        readButton.setOnClickListener {
            authToDecrypt()
        }
    }

    private fun authToEncrypt() {
        try {
            cryptoObjectEncrypt = FingerprintManager.CryptoObject(encryptionObject.cipherForEncryption())
            val fingerprintHandler = FingerprintHandler(this, this, Action.ENCRYPT)
            fingerprintHandler.startAuth(fingerprintManager, cryptoObjectEncrypt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun authToDecrypt() {
        try {
            cryptoObjectDecrypt = FingerprintManager.CryptoObject(
                encryptionObject.cipherForDecryption(
                    pref.getString(SECURE_KEY, null)!!.split(separator)[1].replace("\n", "")
                )
            )
            val fingerprintHandler = FingerprintHandler(this, this, Action.DECRYPT)
            fingerprintHandler.startAuth(fingerprintManager, cryptoObjectDecrypt)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun encryptMessage() {
        try {
            encryptedMessage = encryptionObject.encrypt(
                encryptionObject.cipherEnc,
                input.text.toString().toByteArray(Charsets.UTF_8),
                separator
            )
            editor.putString(SECURE_KEY, encryptedMessage)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decryptMessage() {
        val mess = pref.getString(SECURE_KEY, null)!!.split(separator)[0]
        val decryptedData = encryptionObject.decrypt(
            encryptionObject.cipherDec,
            mess
        )
        showAlertDialog(decryptedData)
    }

    private fun showAlertDialog(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Decrypted message")
        builder.setMessage(message)
        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onAuthenticationSuccess(action: Action) {
        when(action){
            Action.DECRYPT -> decryptMessage()
            Action.ENCRYPT -> {
                encryptMessage()
                showToast("Message encrypted!")
                input.setText("")
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
