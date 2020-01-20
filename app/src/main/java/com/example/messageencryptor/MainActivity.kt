package com.example.messageencryptor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.nio.charset.Charset
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pair = generateKeyPair(2048)
        val privateKey = pair.private
        val publicKey = pair.public

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val publicKeyText = Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
        publicKeyView.text = publicKeyText

        val copyToClipboard = View.OnClickListener {
            val toast =
                Toast.makeText(applicationContext, "Copied to clipboard", Toast.LENGTH_SHORT)
            toast.show()
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText("key", (it as TextView).text.toString()))
        }

        publicKeyView.setOnClickListener(copyToClipboard)

        encryptedTextView.setOnClickListener(copyToClipboard)

        encryptButton.setOnClickListener(View.OnClickListener {
            val msg = textToEncryptView.text.toString()
            val key = encryptKeyView.text.toString()

            if (msg.isEmpty() || key.isEmpty()) {
                return@OnClickListener
            }

            try {
                encryptedTextView.text = Base64.encodeToString(encrypt(msg, key), Base64.NO_WRAP)
            } catch (_: Exception) {}
        })

        decryptButton.setOnClickListener(View.OnClickListener {
            val encryptedMsg = textToDecryptView.text.toString()

            if (encryptedMsg.isEmpty()) {
                return@OnClickListener
            }

            try {
                decryptedTextView.text = decrypt(encryptedMsg, privateKey)
                    .toString(Charset.defaultCharset())
            } catch (_: Exception) {}
        })
    }

    fun generateKeyPair(keyLength: Int): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(keyLength)
        return keyGen.generateKeyPair()
    }

    fun stringToKey(key: String): PublicKey {
        val keySpec = X509EncodedKeySpec(Base64.decode(key, Base64.NO_WRAP))
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }

    fun encrypt(msg: String, key: String): ByteArray {
        val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, stringToKey(key))
        return cipher.doFinal(msg.toByteArray())
    }

    fun decrypt(encryptedMsg: String, key: PrivateKey): ByteArray {
        val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(Base64.decode(encryptedMsg, Base64.NO_WRAP))
    }

}


