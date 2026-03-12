package com.example.myapplication
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        supportActionBar?.title = "Confirmation"

        // Views find karo
        val tvName      = findViewById<TextView>(R.id.tvConfirmName)
        val tvPhone     = findViewById<TextView>(R.id.tvConfirmPhone)
        val tvEmail     = findViewById<TextView>(R.id.tvConfirmEmail)
        val tvEventType = findViewById<TextView>(R.id.tvConfirmEventType)
        val tvDate      = findViewById<TextView>(R.id.tvConfirmDate)
        val tvGender    = findViewById<TextView>(R.id.tvConfirmGender)
        val ivImage     = findViewById<ImageView>(R.id.ivConfirmImage)
        val btnHome     = findViewById<Button>(R.id.btnBackToHome)
        val btnAnother  = findViewById<Button>(R.id.btnRegisterAnother)

        // Intent se data lo aur set karo
        tvName.text      = intent.getStringExtra("fullName")  ?: "N/A"
        tvPhone.text     = intent.getStringExtra("phone")     ?: "N/A"
        tvEmail.text     = intent.getStringExtra("email")     ?: "N/A"
        tvEventType.text = intent.getStringExtra("eventType") ?: "N/A"
        tvDate.text      = intent.getStringExtra("eventDate") ?: "N/A"
        tvGender.text    = intent.getStringExtra("gender")    ?: "N/A"

        // Image set karo agar select ki thi
        val imageUriStr = intent.getStringExtra("imageUri")
        if (!imageUriStr.isNullOrEmpty()) {
            try {
                ivImage.setImageURI(Uri.parse(imageUriStr))
            } catch (e: Exception) {
                // Default image rehne do
            }
        }

        // Home button
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Register Another button
        btnAnother.setOnClickListener {
            val intent = Intent(this, EventRegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}