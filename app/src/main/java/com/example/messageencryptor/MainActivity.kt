package com.example.messageencryptor

import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
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

        publicKeyView.text = Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)

        encryptButton.setOnClickListener(View.OnClickListener {
            val msg = textToDecryptView.text.toString()
            val key = encryptKeyView.text.toString()

            if (msg.isEmpty() || key.isEmpty()) {
                return@OnClickListener
            }

            try {
                encryptedTextView.text = Base64.encodeToString(encrypt(msg, key), Base64.DEFAULT)
            } catch (_: Exception) {}
        })

        decryptButton.setOnClickListener(View.OnClickListener {
            val encryptedMsg = textToDecryptView.text.toString()

            if (encryptedMsg.isEmpty()) {
                return@OnClickListener
            }

            try {
                decryptedTextView.text =
                    Base64.decode(decrypt(encryptedMsg, privateKey), Base64.DEFAULT).toString()
            } catch (_: Exception) {}
        })
    }

    fun generateKeyPair(keyLength: Int): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(keyLength)
        return keyGen.generateKeyPair()
    }

    fun stringToKey(key: String): PublicKey {
        val keySpec = X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT))
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
        return cipher.doFinal(Base64.decode(encryptedMsg, Base64.DEFAULT))
    }
}


