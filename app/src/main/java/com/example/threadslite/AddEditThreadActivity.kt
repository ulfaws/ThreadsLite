package com.example.threadslite

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddEditThreadActivity : AppCompatActivity() {

    private var threadIdFromIntent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        val etContent = findViewById<EditText>(R.id.etContent)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val database = FirebaseDatabase.getInstance().getReference("threads")
        val auth = FirebaseAuth.getInstance()

        threadIdFromIntent = intent.getStringExtra("THREAD_ID")
        val contentFromIntent = intent.getStringExtra("THREAD_CONTENT")

        if (threadIdFromIntent != null) {
            etContent.setText(contentFromIntent)
            btnSave.text = "Update Thread"
        }

        btnSave.setOnClickListener {
            val content = etContent.text.toString().trim()
            val user = auth.currentUser

            if (content.isEmpty()) {
                etContent.error = "Tuliskan sesuatu dulu ya!"
                return@setOnClickListener
            }

            if (user == null) {
                Toast.makeText(this, "Kamu harus login dulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = threadIdFromIntent ?: database.push().key ?: ""

            val threadData = ThreadModel(
                id = id,
                author = user.email ?: "Anonymous",
                content = content,
                uid = user.uid
            )

            database.child(id).setValue(threadData)
                .addOnSuccessListener {
                    showSuccessNotification()

                    val msg =
                        if (threadIdFromIntent == null) "Thread dibuat!" else "Thread diupdate!"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showSuccessNotification() {
        val channelId = "threads_popup_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Threads Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi saat berhasil posting"
                enableLights(true)
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle("ThreadsLite")
            .setContentText("Thread kamu berhasil disimpan!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        nm.notify(1, builder.build())
    }
}
